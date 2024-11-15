package io.quarkiverse.fury.it;

import io.quarkiverse.fury.FurySerialization;

@FurySerialization(targetClasses = ThirdPartyBar.class, serializer = ThridPartyBarSerializer.class)
public class ThirdPartyFooConfig {
}
