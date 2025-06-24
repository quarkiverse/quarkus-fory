package io.quarkiverse.fory.it;

import org.apache.fory.Fory;
import org.apache.fory.memory.MemoryBuffer;
import org.apache.fory.serializer.Serializer;

public class ThridPartyBarSerializer extends Serializer<ThirdPartyBar> {
    public ThridPartyBarSerializer(Fory fory, Class<ThirdPartyBar> type) {
        super(fory, type);
    }

    @Override
    public void write(MemoryBuffer buffer, ThirdPartyBar value) {
        buffer.writeVarInt32(value.f1());
        fory.writeJavaString(buffer, value.f2());
    }

    @Override
    public ThirdPartyBar read(MemoryBuffer buffer) {
        return new ThirdPartyBar(buffer.readVarInt32(), fory.readJavaString(buffer));
    }
}
