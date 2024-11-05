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

    private FurySerializerBuildItem(Class<?> clazz, int classId, Class<? extends Serializer> serializer) {
        this.clazz = clazz;
        this.classId = classId;
        this.serializer = serializer;
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

    public static FurySerializerBuildItem buildItem(ClassInfo classInfo) {
        Class<?> clazz = JandexReflection.loadClass(classInfo);
        FurySerialization annotation = clazz.getDeclaredAnnotation(FurySerialization.class);
        if (annotation.targetClass() != FurySerialization.CurrentAnnotatedTypeStub.class) {
            clazz = annotation.targetClass();
        }
        Class<? extends Serializer> serializer = annotation.serializer();
        if (serializer == Serializer.class) {
            serializer = null;
        }
        return new FurySerializerBuildItem(clazz, annotation.classId(), serializer);
    }
}
