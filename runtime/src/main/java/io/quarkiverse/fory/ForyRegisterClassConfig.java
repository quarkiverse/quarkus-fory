package io.quarkiverse.fory;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface ForyRegisterClassConfig {
    /**
     * Class id must be greater or equal to 256, and it must be different between classes.
     * The default is -1.
     */
    @WithDefault("-1")
    Integer classId();

    /**
     * Specify a customized serializer for current class.
     * This should be empty to let Fory create serializer for current class. But if
     * users want to customize serialization for this class, one can provide serializer here.
     */
    Optional<String> serializer();

}
