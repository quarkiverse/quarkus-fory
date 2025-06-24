package io.quarkiverse.fory;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.apache.fory.BaseFory;

/** Producers of beans that are injectable via CDI. */
@Singleton
public class ForyProducer {
    private volatile BaseFory fory;

    public void setFory(BaseFory fory) {
        this.fory = fory;
    }

    @Singleton
    @Produces
    BaseFory fory() {
        return this.fory;
    }
}
