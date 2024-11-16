package io.quarkiverse.fury.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.serializer.Serializer;

public class FooSerializer extends Serializer<Foo> {
    public FooSerializer(Fury fury, Class<Foo> type) {
        super(fury, type);
    }

    @Override
    public void write(MemoryBuffer buffer, Foo value) {
        buffer.writeVarInt32(value.f1());
        fury.writeJavaString(buffer, value.f2());

        buffer.writeInt32(value.f3().size());
        for (String v : value.f3()) {
            fury.writeJavaString(buffer, v);
        }

        buffer.writeInt32(value.f4().size());
        for (var entry : value.f4().entrySet()) {
            fury.writeJavaString(buffer, entry.getKey());
            buffer.writeInt64(entry.getValue());
        }
    }

    @Override
    public Foo read(MemoryBuffer buffer) {
        int f1 = buffer.readVarInt32();
        String f2 = fury.readJavaString(buffer);
        List<String> f3 = new ArrayList<>();
        Map<String, Long> f4 = new HashMap<>();

        int size = buffer.readInt32();
        for (int i = 0; i < size; i++) {
            f3.add(fury.readJavaString(buffer));
        }

        size = buffer.readInt32();
        for (int i = 0; i < size; i++) {
            f4.put(fury.readJavaString(buffer), buffer.readInt64());
        }

        return new Foo(f1, f2, f3, f4);
    }
}
