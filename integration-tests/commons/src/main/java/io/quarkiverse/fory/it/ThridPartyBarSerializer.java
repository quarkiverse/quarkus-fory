package io.quarkiverse.fory.it;

import org.apache.fory.config.Config;
import org.apache.fory.context.ReadContext;
import org.apache.fory.context.WriteContext;
import org.apache.fory.serializer.Serializer;

public class ThridPartyBarSerializer extends Serializer<ThirdPartyBar> {
    public ThridPartyBarSerializer(Config config, Class<ThirdPartyBar> type) {
        super(config, type);
    }

    @Override
    public void write(WriteContext ctx, ThirdPartyBar value) {
        ctx.getBuffer().writeVarInt32(value.f1());
        ctx.writeString(value.f2());
    }

    @Override
    public ThirdPartyBar read(ReadContext ctx) {
        return new ThirdPartyBar(ctx.getBuffer().readVarInt32(), ctx.readString());
    }
}
