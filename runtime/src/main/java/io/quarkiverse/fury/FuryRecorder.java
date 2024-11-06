package io.quarkiverse.fury;

import java.util.Optional;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.FuryBuilder;
import org.apache.fury.resolver.ClassResolver;
import org.apache.fury.serializer.Serializer;
import org.apache.fury.util.Preconditions;
import org.jboss.logging.Logger;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class FuryRecorder {
    private static final Logger LOG = Logger.getLogger(FuryRecorder.class);

    public RuntimeValue<BaseFury> createFury(
            final FuryBuildTimeConfig config, final BeanContainer beanContainer) {
        // create the Fury instance from the config
        FuryBuilder builder = Fury.builder();
        builder.requireClassRegistration(config.requiredClassRegistration())
                .withRefTracking(config.trackRef())
                .withCompatibleMode(config.compatibleMode())
                .withDeserializeNonexistentClass(config.deserializeNonexistentClass())
                .deserializeNonexistentEnumValueAsNull(config.deserializeNonexistentEnumValueAsNull())
                .withNumberCompressed(config.compressNumber())
                .withStringCompressed(config.compressString());
        BaseFury fury = config.threadSafe() ? builder.buildThreadSafeFury() : builder.build();

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
        BaseFury fury = furyValue.getValue();
        if (classId > 0) {
            Preconditions.checkArgument(
                    classId >= 256 && classId <= Short.MAX_VALUE,
                    "Class id %s must be >= 256 and <= %s",
                    classId,
                    Short.MAX_VALUE);
            Class<?> registeredClass;
            if (fury instanceof ThreadSafeFury) {
                ThreadSafeFury threadSafeFury = (ThreadSafeFury) fury;
                registeredClass = (threadSafeFury).execute(f -> f.getClassResolver().getRegisteredClass((short) classId));
                if (serializer == null) {
                    // Generate serializer bytecode.
                    threadSafeFury.execute(f -> f.getClassResolver().getSerializerClass(clazz));
                }
            } else {
                ClassResolver classResolver = ((Fury) fury).getClassResolver();
                registeredClass = classResolver.getRegisteredClass((short) classId);
                if (serializer == null) {
                    // Generate serializer bytecode.
                    classResolver.getSerializerClass(clazz);
                }
            }
            Preconditions.checkArgument(
                    registeredClass == null,
                    "ClassId %s has been registered for class %s",
                    classId,
                    registeredClass);
            fury.register(clazz, (short) classId);
        } else {
            // Generate serializer bytecode.
            fury.register(clazz, serializer == null);
        }
        if (serializer != null) {
            fury.registerSerializer(clazz, serializer);
        }
    }
}
