package io.quarkiverse.fury.it;

import io.quarkiverse.fury.FurySerialization;

@FurySerialization(targetClass = ThirdPartyBar.class, serializer = ThridPartyBarSerializer.class)
public class ThirdPartyFooConfig {
}
