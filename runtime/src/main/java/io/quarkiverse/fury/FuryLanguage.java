package io.quarkiverse.fury;

import org.apache.fury.config.Language;

public enum FuryLanguage {
    JAVA(Language.JAVA),
    XLANG(Language.XLANG);

    public final Language format;

    FuryLanguage(Language format) {
        this.format = format;
    }
}
