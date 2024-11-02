package io.quarkiverse.fury.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.JandexReflection;

import io.quarkiverse.fury.FurySerialization;
import io.quarkus.builder.item.MultiBuildItem;

public final class FurySerializerBuildItem extends MultiBuildItem {
    private final Class<?> clazz;
    private final int classId;

    public FurySerializerBuildItem(ClassInfo classInfo) {
        clazz = JandexReflection.loadClass(classInfo);
        classId = clazz.getDeclaredAnnotation(FurySerialization.class).classId();
    }

    public int getClassId() {
        return classId;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
