package io.quarkiverse.fury.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.runtime.RuntimeValue;
import org.apache.fury.BaseFury;

/** A build item that represents a Fury instance. */
public final class FuryBuildItem extends SimpleBuildItem {
  private final RuntimeValue<BaseFury> value;

  public FuryBuildItem(RuntimeValue<BaseFury> value) {
    this.value = value;
  }

  public RuntimeValue<BaseFury> getFury() {
    return value;
  }
}
