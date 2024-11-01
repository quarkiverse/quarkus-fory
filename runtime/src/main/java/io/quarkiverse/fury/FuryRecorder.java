package io.quarkiverse.fury;

import org.apache.fury.Fury;
import org.apache.fury.config.FuryBuilder;
import org.apache.fury.util.Preconditions;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class FuryRecorder {
    public RuntimeValue<Fury> createFury(final FuryBuildTimeConfig config, final BeanContainer beanContainer) {
        // create the Fury instance from the config
        FuryBuilder builder = Fury.builder();
        if (config.requiredClassRegistration()) {
            builder.requireClassRegistration(true);
        }
        Fury fury = builder.build();

        // register to the container
        beanContainer.beanInstance(FuryProducer.class).setFury(fury);
        return new RuntimeValue<>(fury);
    }

    public void registerClass(final RuntimeValue<Fury> fury, final Class<?> clazz, final int classId) {
        if (classId > 0 && classId < Short.MAX_VALUE) {
            Preconditions.checkArgument(classId >= 256, "Class id %s must be >= 256", classId);
            Class<?> registeredClass = fury.getValue().getClassResolver().getRegisteredClass((short) classId);
            Preconditions.checkArgument(registeredClass == null,
                    "ClassId %s has been registered for class %s", classId, registeredClass);
        } else {
            fury.getValue().register(clazz, true);
        }

    }
}
