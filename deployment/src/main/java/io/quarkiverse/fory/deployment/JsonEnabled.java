package io.quarkiverse.fory.deployment;

import java.util.function.BooleanSupplier;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.bootstrap.classloading.QuarkusClassLoader;

public class JsonEnabled implements BooleanSupplier {
    @Override
    public boolean getAsBoolean() {
        return ConfigProvider.getConfig().getOptionalValue("quarkus.fory.json.enabled", Boolean.class).orElse(Boolean.FALSE)
                && QuarkusClassLoader.isClassPresentAtRuntime("org.apache.fory.json.ForyJson");
    }
}
