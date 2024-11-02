package io.quarkiverse.fury;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a class as a Fury serializable. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FurySerialization {
    /** Class id must be greater or equal to 256, and it must be different between classes. */
    int classId() default -1;
}
