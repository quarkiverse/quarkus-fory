package io.quarkiverse.fory.it.json;

import org.apache.fory.json.annotation.JsonType;

@JsonType
public record JsonTypeModel(int n, String s) {
}
