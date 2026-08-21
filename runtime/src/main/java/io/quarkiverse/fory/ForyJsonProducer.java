package io.quarkiverse.fory;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.apache.fory.json.ForyJson;

/**
 * CDI producer for the {@link ForyJson} instance.
 * <p>
 * The instance is thread-safe and reusable. It is created once at startup
 * and shared across all requests.
 */
@Singleton
public class ForyJsonProducer {

    private volatile ForyJson foryJson;

    public void setForyJson(ForyJson foryJson) {
        this.foryJson = foryJson;
    }

    @Singleton
    @Produces
    ForyJson foryJson() {
        if (this.foryJson == null) {
            // Fallback: create a default instance if recorder didn't set one
            this.foryJson = ForyJson.builder().build();
        }
        return this.foryJson;
    }
}
