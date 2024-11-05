package io.quarkiverse.fury.it;

import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.serializer.Serializer;

public class ThridPartyBarSerializer extends Serializer<ThirdPartyBar> {
    public ThridPartyBarSerializer(Fury fury, Class<ThirdPartyBar> type) {
        super(fury, type);
    }

    @Override
    public void write(MemoryBuffer buffer, ThirdPartyBar value) {
        buffer.writeVarInt32(value.f1());
        fury.writeJavaString(buffer, value.f2());
    }

    @Override
    public ThirdPartyBar read(MemoryBuffer buffer) {
        return new ThirdPartyBar(buffer.readVarInt32(), fury.readJavaString(buffer));
    }
}
