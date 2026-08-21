package io.quarkiverse.fory.it.json;

/** Deliberately not annotated with {@code @ForySerialization}: Fory JSON needs no class registration. */
public record Plain(int n, String s) {
}
