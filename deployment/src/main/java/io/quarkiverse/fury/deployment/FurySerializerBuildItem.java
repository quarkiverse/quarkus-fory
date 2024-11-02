package io.quarkiverse.fury.deployment;

import org.apache.fury.serializer.Serializer;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.JandexReflection;

import io.quarkiverse.fury.FurySerialization;
import io.quarkus.builder.item.MultiBuildItem;

public final class FurySerializerBuildItem extends MultiBuildItem {
    private final Class<?> clazz;
    private final int classId;
    private final Class<? extends Serializer> serializer;

    public FurySerializerBuildItem(ClassInfo classInfo) {
        clazz = JandexReflection.loadClass(classInfo);
        FurySerialization annotation = clazz.getDeclaredAnnotation(FurySerialization.class);
        classId = annotation.classId();
        if (annotation.serializer() == Serializer.class) {
            serializer = null;
        } else {
            serializer = annotation.serializer();
        }
    }

    public int getClassId() {
        return classId;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Class<? extends Serializer> getSerializer() {
        return serializer;
    }
}
