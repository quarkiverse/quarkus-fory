package io.quarkiverse.fory.it;

import org.apache.fory.config.Config;
import org.apache.fory.context.ReadContext;
import org.apache.fory.context.WriteContext;
import org.apache.fory.serializer.Serializer;

public class BarSerializer extends Serializer<Bar> {
    public BarSerializer(Config config, Class<Bar> type) {
        super(config, type);
    }

    @Override
    public void write(WriteContext ctx, Bar value) {
        ctx.getBuffer().writeVarInt32(value.f1());
        ctx.writeString(value.f2());
    }

    @Override
    public Bar read(ReadContext ctx) {
        return new Bar(ctx.getBuffer().readVarInt32(), ctx.readString());
    }
}
