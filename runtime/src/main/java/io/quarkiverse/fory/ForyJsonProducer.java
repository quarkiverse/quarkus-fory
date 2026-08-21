package io.quarkiverse.fory;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.apache.fory.json.ForyJson;

/**
 * CDI producer for the {@link ForyJson} instance.
 * <p>
 * The instance is thread-safe, immutable, and shared across all requests via singleton scope.
 */
@Singleton
public class ForyJsonProducer {

    @Singleton
    @Produces
    ForyJson foryJson() {
        return ForyJson.builder().build();
    }
}
