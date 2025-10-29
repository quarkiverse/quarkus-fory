package io.quarkiverse.fory;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

import org.apache.fory.BaseFory;
import org.apache.fory.Fory;
import org.apache.fory.ThreadLocalFory;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.config.Config;
import org.apache.fory.config.ForyBuilder;
import org.apache.fory.resolver.ClassChecker;
import org.apache.fory.resolver.ClassResolver;
import org.apache.fory.serializer.Serializer;
import org.apache.fory.util.GraalvmSupport;
import org.apache.fory.util.Preconditions;
import org.jboss.logging.Logger;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ForyRecorder {
    private static final Logger LOG = Logger.getLogger(ForyRecorder.class);
    private static final ConcurrentSkipListSet<String> annotatedClasses = new ConcurrentSkipListSet<>();
    private static final ClassChecker checker = (classResolver, className) -> !GraalvmSupport.isGraalRuntime()
            || annotatedClasses.contains(className);

    public RuntimeValue<BaseFory> createFory(
            final ForyBuildTimeConfig config, final BeanContainer beanContainer) {
        // create the Fory instance from the config
        ForyBuilder builder = Fory.builder();
        builder.requireClassRegistration(config.requiredClassRegistration())
                .withLanguage(config.language().format)
                .withName("Fory-" + System.nanoTime())
                .withRefTracking(config.trackRef())
                .withCompatibleMode(config.compatibleMode())
                .withDeserializeNonexistentClass(config.deserializeNonexistentClass())
                .deserializeNonexistentEnumValueAsNull(config.deserializeNonexistentEnumValueAsNull())
                .withNumberCompressed(config.compressNumber())
                .withStringCompressed(config.compressString());
        Function<ClassLoader, Fory> foryFactory = c -> {
            Fory f = builder.withClassLoader(c).build();
            f.getClassResolver().setClassChecker(checker);
            return f;
        };
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BaseFory fory = config.threadSafe() ? new ThreadLocalFory(foryFactory) : foryFactory.apply(classLoader);
        // register to the container
        beanContainer.beanInstance(ForyProducer.class).setFory(fory);
        return new RuntimeValue<>(fory);
    }

    public void registerClassByName(final RuntimeValue<BaseFory> foryValue, final String className, final int classId,
            final Optional<String> serializerClassName) {
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Class<? extends Serializer> serializer = null;
            if (serializerClassName.isPresent()) {
                serializer = (Class<? extends Serializer>) Thread.currentThread().getContextClassLoader()
                        .loadClass(serializerClassName.get());
            }
            registerClass(foryValue, clazz, classId, serializer);
            annotatedClasses.add(className);
        } catch (ClassNotFoundException e) {
            LOG.warn("can not find register class: " + className
                    + (serializerClassName.isPresent()
                            ? " with serializer: " + serializerClassName.get()
                            : ""));
            throw new RuntimeException(e);
        }
    }

    public void registerClass(
            final RuntimeValue<BaseFory> foryValue, final Class<?> clazz,
            final int classId, Class<? extends Serializer> serializer) {
        annotatedClasses.add(clazz.getName());
        BaseFory fory = foryValue.getValue();
        ClassResolver classResolver = getClassResolver(fory);
        Config config = classResolver.getFory().getConfig();
        if (classId > 0) {
            Preconditions.checkArgument(
                    classId >= 256 && classId <= Short.MAX_VALUE,
                    "Class id %s must be >= 256 and <= %s",
                    classId,
                    Short.MAX_VALUE);
            Class<?> registeredClass = classResolver.getRegisteredClass((short) classId);
            Preconditions.checkArgument(
                    registeredClass == null,
                    "ClassId %s has been registered for class %s",
                    classId,
                    registeredClass);
            fory.register(clazz, (short) classId);
        } else {
            // Generate serializer bytecode.
            if (config.requireClassRegistration()) {
                fory.register(clazz, serializer == null);
            }
        }
        if (serializer != null) {
            fory.registerSerializer(clazz, serializer);
        }
        // Note: In Apache Fory 0.13.0+, serializer generation is handled automatically
        // when registering classes, so explicit createSerializerAhead call is no longer needed
    }

    private ClassResolver getClassResolver(BaseFory fory) {
        if (fory instanceof ThreadSafeFory) {
            ThreadSafeFory threadSafeFory = (ThreadSafeFory) fory;
            return threadSafeFory.execute(Fory::getClassResolver);
        } else {
            return ((Fory) fory).getClassResolver();
        }
    }
}
