package io.quarkiverse.fory.it;

import io.quarkiverse.fory.ForySerialization;

@ForySerialization(targetClasses = ThirdPartyBar.class, serializer = ThridPartyBarSerializer.class)
public class ThirdPartyFooConfig {
}
