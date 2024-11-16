package io.quarkiverse.fury.it;

import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.serializer.Serializer;

public class BarSerializer extends Serializer<Bar> {
    public BarSerializer(Fury fury, Class<Bar> type) {
        super(fury, type);
    }

    @Override
    public void write(MemoryBuffer buffer, Bar value) {
        buffer.writeVarInt32(value.f1());
        fury.writeJavaString(buffer, value.f2());
    }

    @Override
    public Bar read(MemoryBuffer buffer) {
        return new Bar(buffer.readVarInt32(), fury.readJavaString(buffer));
    }
}
