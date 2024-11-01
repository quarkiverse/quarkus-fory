package io.quarkiverse.fury;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.apache.fury.Fury;

/**
 * Producers of beans that are injectable via CDI.
 */
@Singleton
public class FuryProducer {
    private volatile Fury fury;

    public void setFury(Fury fury) {
        this.fury = fury;
    }

    @Singleton
    @Produces
    Fury fury() {
        return this.fury;
    }

}
