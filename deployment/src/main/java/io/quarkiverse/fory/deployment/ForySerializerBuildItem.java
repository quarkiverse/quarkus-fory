package io.quarkiverse.fory.deployment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.fory.serializer.Serializer;
import org.apache.fory.util.Preconditions;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.JandexReflection;

import io.quarkiverse.fory.ForySerialization;
import io.quarkus.builder.item.MultiBuildItem;

public final class ForySerializerBuildItem extends MultiBuildItem {
    private final Class<?> clazz;
    private final int classId;
    private final Class<? extends Serializer> serializer;

    private ForySerializerBuildItem(Class<?> clazz, int classId, Class<? extends Serializer> serializer) {
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

    public static List<ForySerializerBuildItem> buildItems(ClassInfo classInfo) {
        Class<?> clazz = JandexReflection.loadClass(classInfo);
        ForySerialization annotation = clazz.getDeclaredAnnotation(ForySerialization.class);
        Class<?>[] classes = annotation.targetClasses();
        int classId = annotation.classId();
        Class<? extends Serializer> serializer = annotation.serializer();
        if (classes.length > 1) {
            Preconditions.checkArgument(classId == -1,
                    "Class %s is must not be specified when multiple `targetClasses` %s are specified",
                    classId, classes);
            return Arrays.stream(classes)
                    .map(clz -> new ForySerializerBuildItem(clz, -1, serializer))
                    .collect(Collectors.toList());
        } else {
            if (classes.length == 1) {
                clazz = classes[0];
            }
            return Collections.singletonList(new ForySerializerBuildItem(clazz, classId, serializer));
        }
    }
}
