package io.quarkus.fury.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import org.apache.fury.Fury;
import org.apache.fury.serializer.Serializer;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.JandexReflection;

public class FurySerializerBuildItem extends SimpleBuildItem {
  private static final Fury fury = Fury.builder().build();

  private final Class<? extends Serializer> serializerClass;

  public FurySerializerBuildItem(ClassInfo classInfo) {
    Class<?> clazz = JandexReflection.loadClass(classInfo);
    fury.register(clazz, true);
    serializerClass = fury.getClassResolver().getSerializerClass(clazz);
  }
}
