package io.quarkiverse.fury;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
@ConfigMapping(prefix = "quarkus.fury")
public interface FuryBuildTimeConfig {
    /** Require class registration for serialization. The default is true. */
    @WithDefault("true")
    boolean requiredClassRegistration();

    /** Whether to use thread safe fury. The default is true. */
    @WithDefault("true")
    boolean threadSafe();

    /**
     * Names of classes to register which no need to be with class-id or customize serializer.
     */
    Optional<String> registerClassNames();

    /**
     * Configurations of register classes
     */
    @ConfigDocSection
    @ConfigDocMapKey("register-class-name")
    Map<String, FuryRegisterClassConfig> registerClasses();

}
