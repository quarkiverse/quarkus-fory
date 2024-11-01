package io.quarkiverse.fury.deployment;

import org.apache.fury.Fury;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.runtime.RuntimeValue;

/**
 * A build item that represents a Fury instance.
 */
public final class FuryBuildItem extends SimpleBuildItem {
    final private RuntimeValue<Fury> value;

    public FuryBuildItem(RuntimeValue<Fury> value) {
        this.value = value;
    }

    public RuntimeValue<Fury> getFury() {
        return value;
    }
}
