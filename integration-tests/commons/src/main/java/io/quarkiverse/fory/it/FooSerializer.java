package io.quarkiverse.fory.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fory.Fory;
import org.apache.fory.memory.MemoryBuffer;
import org.apache.fory.serializer.Serializer;

public class FooSerializer extends Serializer<Foo> {
    public FooSerializer(Fory fory, Class<Foo> type) {
        super(fory, type);
    }

    @Override
    public void write(MemoryBuffer buffer, Foo value) {
        buffer.writeVarInt32(value.f1());
        fory.writeJavaString(buffer, value.f2());

        buffer.writeInt32(value.f3().size());
        for (String v : value.f3()) {
            fory.writeJavaString(buffer, v);
        }

        buffer.writeInt32(value.f4().size());
        for (var entry : value.f4().entrySet()) {
            fory.writeJavaString(buffer, entry.getKey());
            buffer.writeInt64(entry.getValue());
        }
    }

    @Override
    public Foo read(MemoryBuffer buffer) {
        int f1 = buffer.readVarInt32();
        String f2 = fory.readJavaString(buffer);
        List<String> f3 = new ArrayList<>();
        Map<String, Long> f4 = new HashMap<>();

        int size = buffer.readInt32();
        for (int i = 0; i < size; i++) {
            f3.add(fory.readJavaString(buffer));
        }

        size = buffer.readInt32();
        for (int i = 0; i < size; i++) {
            f4.put(fory.readJavaString(buffer), buffer.readInt64());
        }

        return new Foo(f1, f2, f3, f4);
    }
}
