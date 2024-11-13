package io.quarkiverse.fury;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@Dependent
public class ClassicFurySerializerProducer {
    @Produces
    @ApplicationScoped
    public ClassicFurySerializer create() {
        return new ClassicFurySerializer();
    }
}
