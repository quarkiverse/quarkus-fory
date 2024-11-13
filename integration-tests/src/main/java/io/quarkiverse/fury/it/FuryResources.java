package io.quarkiverse.fury.it;

import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.serializer.Serializer;
import org.apache.fury.util.Preconditions;

@Path("/fury")
@ApplicationScoped
public class FuryResources {
    public final static short BAR_CLASS_ID = 400;
    @Inject
    BaseFury fury;

    @GET
    @Path("/record")
    public Boolean testSerializeFooRecord() {
        Foo foo1 = new Foo(10, "abc", List.of("str1", "str2"), Map.of("k1", 10L, "k2", 20L));
        Foo foo2 = (Foo) fury.deserialize(fury.serialize(foo1));
        Serializer serializer;
        if (fury instanceof ThreadSafeFury) {
            serializer = ((ThreadSafeFury) fury).execute(f -> f.getClassResolver().getSerializer(Foo.class));
        } else {
            serializer = ((Fury) fury).getClassResolver().getSerializer(Foo.class);
        }
        Preconditions.checkArgument(serializer instanceof FooSerializer, serializer);

        return foo1.equals(foo2);
    }

    @GET
    @Path("/bar")
    public Boolean testSerializeBarRecord() {
        Bar bar = new Bar(10, "abc");
        Bar bar2 = (Bar) fury.deserialize(fury.serialize(bar));
        Serializer serializer;
        if (fury instanceof ThreadSafeFury) {
            serializer = ((ThreadSafeFury) fury).execute(f -> f.getClassResolver().getSerializer(Bar.class));
        } else {
            serializer = ((Fury) fury).getClassResolver().getSerializer(Bar.class);
        }
        Preconditions.checkArgument(serializer instanceof BarSerializer, serializer);
        return bar2.equals(bar);
    }

    @GET
    @Path("/third_party_bar")
    public Boolean testSerializeThirdPartyBar() {
        ThirdPartyBar bar = new ThirdPartyBar(10, "abc");
        ThirdPartyBar bar2 = (ThirdPartyBar) fury.deserialize(fury.serialize(bar));
        Serializer<?> serializer;
        if (fury instanceof ThreadSafeFury) {
            serializer = ((ThreadSafeFury) fury).execute(f -> f.getClassResolver().getSerializer(ThirdPartyBar.class));
        } else {
            serializer = ((Fury) fury).getClassResolver().getSerializer(ThirdPartyBar.class);
        }
        Preconditions.checkArgument(serializer instanceof ThridPartyBarSerializer, serializer);
        return bar2.equals(bar);
    }

    @GET
    @Path("/pojo")
    public Boolean testSerializePOJO() {
        Struct struct1 = Struct.create();
        Struct struct2 = (Struct) fury.deserialize(fury.serialize(struct1));

        return struct1.equals(struct2);
    }

    @GET
    @Path("/test")
    @Produces("application/fury")
    @Consumes("application/fury")
    public Bar testBar(Bar obj) {
        Preconditions.checkArgument(obj.f1() == 1, obj);
        Preconditions.checkArgument(obj.f2().equals("hello bar"), obj);

        return new Bar(2, "bye bar");
    }
}
