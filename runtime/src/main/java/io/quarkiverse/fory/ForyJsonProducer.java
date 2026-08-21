package io.quarkiverse.fory;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.apache.fory.json.ForyJson;

/**
 * CDI producer for the {@link ForyJson} instance.
 * <p>
 * The instance is thread-safe, immutable, and created eagerly at startup.
 * It is shared across all requests via singleton scope.
 */
@Singleton
public class ForyJsonProducer {

    private volatile ForyJson foryJson;

    public void setForyJson(ForyJson foryJson) {
        this.foryJson = foryJson;
    }

    @PostConstruct
    void init() {
        if (this.foryJson == null) {
            this.foryJson = ForyJson.builder().build();
        }
    }

    @Singleton
    @Produces
    ForyJson foryJson() {
        return this.foryJson;
    }
}
