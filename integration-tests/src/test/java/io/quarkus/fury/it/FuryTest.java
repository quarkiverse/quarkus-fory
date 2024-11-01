package io.quarkus.fury.it;

import io.quarkiverse.fury.it.Foo;
import io.quarkiverse.fury.it.Struct;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

@QuarkusTest
public class FuryTest {
    private static final ThreadSafeFury fury = Fury.builder()
      .requireClassRegistration(false).buildThreadSafeFury();

    @Test
    public void testSerializeFooRecord() {
        Foo foo = new Foo(10, "abc", List.of("str1", "str2"), Map.of("k1", 10L, "k2", 20L));
        assertEquals(fury.deserialize(fury.serialize(foo)), foo);
    }

    @Test
    public void testSerializePOJO() {
        Struct struct = Struct.create();
        assertEquals(fury.deserialize(fury.serialize(struct)), struct);
    }

}
