package io.quarkiverse.fory.deployment;

import org.apache.fory.BaseFory;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.runtime.RuntimeValue;

/** A build item that represents a Fory instance. */
public final class ForyBuildItem extends SimpleBuildItem {
    private final RuntimeValue<BaseFory> value;

    public ForyBuildItem(RuntimeValue<BaseFory> value) {
        this.value = value;
    }

    public RuntimeValue<BaseFory> getFory() {
        return value;
    }
}
