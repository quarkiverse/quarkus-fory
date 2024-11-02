package io.quarkiverse.fury;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.FuryBuilder;
import org.apache.fury.resolver.ClassResolver;
import org.apache.fury.serializer.Serializer;
import org.apache.fury.util.Preconditions;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class FuryRecorder {
    public RuntimeValue<BaseFury> createFury(
            final FuryBuildTimeConfig config, final BeanContainer beanContainer) {
        // create the Fury instance from the config
        FuryBuilder builder = Fury.builder();
        builder.requireClassRegistration(config.requiredClassRegistration());
        BaseFury fury = config.threadSafe() ? builder.buildThreadSafeFury() : builder.build();

        // register to the container
        beanContainer.beanInstance(FuryProducer.class).setFury(fury);
        return new RuntimeValue<>(fury);
    }

    public void registerClass(
            final RuntimeValue<BaseFury> fury, final Class<?> clazz,
            final int classId, Class<? extends Serializer> serializer) {
        BaseFury furyValue = fury.getValue();
        if (classId > 0) {
            Preconditions.checkArgument(
                    classId >= 256 && classId <= Short.MAX_VALUE,
                    "Class id %s must be >= 256 and <= %s",
                    classId,
                    Short.MAX_VALUE);
            Class<?> registeredClass;
            if (fury instanceof ThreadSafeFury) {
                ThreadSafeFury threadSafeFury = (ThreadSafeFury) furyValue;
                registeredClass = (threadSafeFury).execute(f -> f.getClassResolver().getRegisteredClass((short) classId));
                // Generate serializer bytecode.
                threadSafeFury.execute(f -> f.getClassResolver().getSerializerClass(clazz));
            } else {
                ClassResolver classResolver = ((Fury) furyValue).getClassResolver();
                registeredClass = classResolver.getRegisteredClass((short) classId);
                // Generate serializer bytecode.
                classResolver.getSerializerClass(clazz);
            }
            Preconditions.checkArgument(
                    registeredClass == null,
                    "ClassId %s has been registered for class %s",
                    classId,
                    registeredClass);
        } else {
            // Generate serializer bytecode.
            furyValue.register(clazz, true);
        }
        furyValue.registerSerializer(clazz, serializer);
    }
}
