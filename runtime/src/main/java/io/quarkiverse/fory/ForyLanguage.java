package io.quarkiverse.fory;

import org.apache.fory.config.Language;

public enum ForyLanguage {
    JAVA(Language.JAVA),
    XLANG(Language.XLANG);

    public final Language format;

    ForyLanguage(Language format) {
        this.format = format;
    }
}
