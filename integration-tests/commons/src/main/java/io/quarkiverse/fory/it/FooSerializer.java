package io.quarkiverse.fory.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fory.config.Config;
import org.apache.fory.context.ReadContext;
import org.apache.fory.context.WriteContext;
import org.apache.fory.memory.MemoryBuffer;
import org.apache.fory.serializer.Serializer;

public class FooSerializer extends Serializer<Foo> {
    public FooSerializer(Config config, Class<Foo> type) {
        super(config, type);
    }

    @Override
    public void write(WriteContext ctx, Foo value) {
        MemoryBuffer buffer = ctx.getBuffer();
        buffer.writeVarInt32(value.f1());
        ctx.writeString(value.f2());

        buffer.writeInt32(value.f3().size());
        for (String v : value.f3()) {
            ctx.writeString(v);
        }

        buffer.writeInt32(value.f4().size());
        for (var entry : value.f4().entrySet()) {
            ctx.writeString(entry.getKey());
            buffer.writeInt64(entry.getValue());
        }
    }

    @Override
    public Foo read(ReadContext ctx) {
        MemoryBuffer buffer = ctx.getBuffer();
        int f1 = buffer.readVarInt32();
        String f2 = ctx.readString();
        List<String> f3 = new ArrayList<>();
        Map<String, Long> f4 = new HashMap<>();

        int size = buffer.readInt32();
        for (int i = 0; i < size; i++) {
            f3.add(ctx.readString());
        }

        size = buffer.readInt32();
        for (int i = 0; i < size; i++) {
            f4.put(ctx.readString(), buffer.readInt64());
        }

        return new Foo(f1, f2, f3, f4);
    }
}
