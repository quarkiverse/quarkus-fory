package io.quarkus.fury.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.fury.runtime.FurySerialization;
import org.apache.fury.Fury;
import org.apache.fury.serializer.Serializer;
import org.apache.fury.util.Preconditions;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.JandexReflection;

public class FurySerializerBuildItem extends SimpleBuildItem {
  private static final Fury fury = Fury.builder().build();

  private final Class<? extends Serializer> serializerClass;

  public FurySerializerBuildItem(ClassInfo classInfo) {
    Class<?> clazz = JandexReflection.loadClass(classInfo);
    FurySerialization annotation = clazz.getDeclaredAnnotation(FurySerialization.class);
    int classId = annotation.classId();
    if (classId > 0 && classId < Short.MAX_VALUE) {
      Class<?> registeredClass = fury.getClassResolver().getRegisteredClass((short) classId);
      Preconditions.checkArgument(registeredClass == null,
        "ClassId %s has been registered for class %s", classId, registeredClass);
    } else {
      fury.register(clazz, (short) classId, true);
    }
    serializerClass = fury.getClassResolver().getSerializerClass(clazz);
  }
}
