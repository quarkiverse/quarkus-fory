package io.quarkiverse.fury;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;
import org.apache.fury.ThreadLocalFury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Config;
import org.apache.fury.config.FuryBuilder;
import org.apache.fury.resolver.ClassChecker;
import org.apache.fury.resolver.ClassResolver;
import org.apache.fury.serializer.Serializer;
import org.apache.fury.util.GraalvmSupport;
import org.apache.fury.util.Preconditions;
import org.jboss.logging.Logger;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class FuryRecorder {
    private static final Logger LOG = Logger.getLogger(FuryRecorder.class);
    private static final ConcurrentSkipListSet<String> annotatedClasses = new ConcurrentSkipListSet<>();
    private static final ClassChecker checker = (classResolver, className) -> !GraalvmSupport.isGraalRuntime()
            || annotatedClasses.contains(className);

    public RuntimeValue<BaseFury> createFury(
            final FuryBuildTimeConfig config, final BeanContainer beanContainer) {
        // create the Fury instance from the config
        FuryBuilder builder = Fury.builder();
        builder.requireClassRegistration(config.requiredClassRegistration())
                .withLanguage(config.language().format)
                .withName("Fury-" + System.nanoTime())
                .withRefTracking(config.trackRef())
                .withCompatibleMode(config.compatibleMode())
                .withDeserializeNonexistentClass(config.deserializeNonexistentClass())
                .deserializeNonexistentEnumValueAsNull(config.deserializeNonexistentEnumValueAsNull())
                .withNumberCompressed(config.compressNumber())
                .withStringCompressed(config.compressString());
        Function<ClassLoader, Fury> furyFactory = c -> {
            Fury f = builder.withClassLoader(c).build();
            f.getClassResolver().setClassChecker(checker);
            return f;
        };
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BaseFury fury = config.threadSafe() ? new ThreadLocalFury(furyFactory) : furyFactory.apply(classLoader);
        // register to the container
        beanContainer.beanInstance(FuryProducer.class).setFury(fury);
        return new RuntimeValue<>(fury);
    }

    public void registerClassByName(final RuntimeValue<BaseFury> furyValue, final String className, final int classId,
            final Optional<String> serializerClassName) {
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Class<? extends Serializer> serializer = null;
            if (serializerClassName.isPresent()) {
                serializer = (Class<? extends Serializer>) Thread.currentThread().getContextClassLoader()
                        .loadClass(serializerClassName.get());
            }
            registerClass(furyValue, clazz, classId, serializer);
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
            final RuntimeValue<BaseFury> furyValue, final Class<?> clazz,
            final int classId, Class<? extends Serializer> serializer) {
        annotatedClasses.add(clazz.getName());
        BaseFury fury = furyValue.getValue();
        ClassResolver classResolver = getClassResolver(fury);
        Config config = classResolver.getFury().getConfig();
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
            fury.register(clazz, (short) classId);
        } else {
            // Generate serializer bytecode.
            if (config.requireClassRegistration()) {
                fury.register(clazz, serializer == null);
            }
        }
        if (serializer != null) {
            fury.registerSerializer(clazz, serializer);
        } else {
            // Generate serializer bytecode.
            try {
                Method createSerializerAhead = ClassResolver.class.getDeclaredMethod(
                        "createSerializerAhead", Class.class);
                createSerializerAhead.setAccessible(true);
                createSerializerAhead.invoke(classResolver, clazz);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ClassResolver getClassResolver(BaseFury fury) {
        if (fury instanceof ThreadSafeFury) {
            ThreadSafeFury threadSafeFury = (ThreadSafeFury) fury;
            return threadSafeFury.execute(Fury::getClassResolver);
        } else {
            return ((Fury) fury).getClassResolver();
        }
    }
}
