package io.quarkiverse.fory.it;

import org.apache.fory.Fory;
import org.apache.fory.memory.MemoryBuffer;
import org.apache.fory.serializer.Serializer;

public class BarSerializer extends Serializer<Bar> {
    public BarSerializer(Fory fory, Class<Bar> type) {
        super(fory, type);
    }

    @Override
    public void write(MemoryBuffer buffer, Bar value) {
        buffer.writeVarInt32(value.f1());
        fory.writeJavaString(buffer, value.f2());
    }

    @Override
    public Bar read(MemoryBuffer buffer) {
        return new Bar(buffer.readVarInt32(), fory.readJavaString(buffer));
    }
}
