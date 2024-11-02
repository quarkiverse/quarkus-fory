package io.quarkiverse.fury;

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
}
