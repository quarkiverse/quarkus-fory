package io.quarkiverse.fury;

import java.util.Map;
import java.util.Optional;

import org.apache.fury.config.CompatibleMode;

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

    /** Whether track shared or circular references. */
    @WithDefault("false")
    boolean trackRef();

    /**
     * Set class schema compatible mode.
     * <br>
     * SCHEMA_CONSISTENT: Class schema must be consistent between serialization peer and deserialization peer.
     * <br>
     * COMPATIBLE: Class schema can be different between serialization peer and deserialization peer. They can
     * add/delete fields independently.
     */
    @WithDefault("SCHEMA_CONSISTENT")
    CompatibleMode compatibleMode();

    /** Use variable length encoding for int/long. */
    @WithDefault("true")
    boolean compressNumber();

    /** Whether compress string for small size. */
    @WithDefault("true")
    boolean compressString();

    /**
     * Whether deserialize/skip data of un-existed class. If not enabled, an exception will be thrown
     * if class not exist.
     */
    @WithDefault("false")
    boolean deserializeNonexistentClass();

    /** If an enum value doesn't exist, return a null instead of throws exception. */
    @WithDefault("false")
    boolean deserializeNonexistentEnumValueAsNull();

    /** Whether to use thread safe fury. The default is true. */
    @WithDefault("true")
    boolean threadSafe();

    /**
     * Names of classes to register which no need to be with class-id or customize serializer.
     * It has to be separated by comma.
     */
    Optional<String> registerClassNames();

    /**
     * Configurations of register class
     */
    @ConfigDocSection
    @ConfigDocMapKey("register-class-name")
    Map<String, FuryRegisterClassConfig> registerClass();

}
