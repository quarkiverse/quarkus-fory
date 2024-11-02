package io.quarkiverse.fury;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.fury.serializer.Serializer;

/** Marks a class as a Fury serializable. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FurySerialization {
    /** Class id must be greater or equal to 256, and it must be different between classes. */
    int classId() default -1;

    /**
     * Specify a customized serializer for current class.
     * This should be null to let Fury create serializer for current class. But if
     * users want to customize serialization for this class, one can provide serializer here.
     */
    Class<? extends Serializer> serializer() default Serializer.class;
}
