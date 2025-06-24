package io.quarkiverse.fory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.fory.serializer.Serializer;

/** Marks a class as a Fory serializable. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForySerialization {
    /** Class id must be greater or equal to 256, and it must be different between classes. */
    int classId() default -1;

    /**
     * Specify a customized serializer for current class.
     * This should be null to let Fory create serializer for current class. But if
     * users want to customize serialization for this class, one can provide serializer here.
     */
    Class<? extends Serializer> serializer() default Serializer.class;

    /**
     * Specify which classes the provided serialized is used for.
     * If not specified, the current annotated class is used.
     */
    Class<?>[] targetClasses() default {};

}
