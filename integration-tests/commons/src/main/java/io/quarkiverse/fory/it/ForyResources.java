package io.quarkiverse.fory.it;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.apache.fory.BaseFory;
import org.apache.fory.Fory;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.serializer.Serializer;
import org.apache.fory.util.Preconditions;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/fory")
public class ForyResources {
    public final static short BAR_CLASS_ID = 400;
    @Inject
    BaseFory fory;

    @Inject
    @RestClient
    Client client;

    @GET
    @Path("/record")
    public Boolean testSerializeFooRecord() {
        Foo foo1 = new Foo(10, "abc", List.of("str1", "str2"), Map.of("k1", 10L, "k2", 20L));
        Foo foo2 = (Foo) fory.deserialize(fory.serialize(foo1));
        Serializer serializer;
        if (fory instanceof ThreadSafeFory) {
            serializer = ((ThreadSafeFory) fory).execute(f -> f.getClassResolver().getSerializer(Foo.class));
        } else {
            serializer = ((Fory) fory).getClassResolver().getSerializer(Foo.class);
        }
        Preconditions.checkArgument(serializer instanceof FooSerializer, serializer);

        return foo1.equals(foo2);
    }

    @GET
    @Path("/bar")
    public Boolean testSerializeBarRecord() {
        Bar bar = new Bar(10, "abc");
        Bar bar2 = (Bar) fory.deserialize(fory.serialize(bar));
        Serializer serializer;
        if (fory instanceof ThreadSafeFory) {
            serializer = ((ThreadSafeFory) fory).execute(f -> f.getClassResolver().getSerializer(Bar.class));
        } else {
            serializer = ((Fory) fory).getClassResolver().getSerializer(Bar.class);
        }
        Preconditions.checkArgument(serializer instanceof BarSerializer, serializer);
        return bar2.equals(bar);
    }

    @GET
    @Path("/third_party_bar")
    public Boolean testSerializeThirdPartyBar() {
        ThirdPartyBar bar = new ThirdPartyBar(10, "abc");
        ThirdPartyBar bar2 = (ThirdPartyBar) fory.deserialize(fory.serialize(bar));
        Serializer<?> serializer;
        if (fory instanceof ThreadSafeFory) {
            serializer = ((ThreadSafeFory) fory).execute(f -> f.getClassResolver().getSerializer(ThirdPartyBar.class));
        } else {
            serializer = ((Fory) fory).getClassResolver().getSerializer(ThirdPartyBar.class);
        }
        Preconditions.checkArgument(serializer instanceof ThridPartyBarSerializer, serializer);
        return bar2.equals(bar);
    }

    @GET
    @Path("/pojo")
    public Boolean testSerializePOJO() {
        Struct struct1 = Struct.create();
        Struct struct2 = (Struct) fory.deserialize(fory.serialize(struct1));

        return struct1.equals(struct2);
    }

    @POST
    @Path("/struct")
    @Produces("application/fory")
    @Consumes("application/fory")
    public Struct testStruct(Struct obj) {
        Preconditions.checkArgument(obj.equals(Struct.create()), obj);
        return Struct.create();
    }

    @POST
    @Path("/test")
    @Produces("application/fory")
    @Consumes("application/fory")
    public Bar testBar(Bar obj) {
        Preconditions.checkArgument(obj.f1() == 1, obj);
        Preconditions.checkArgument(obj.f2().equals("hello bar"), obj);

        return new Bar(2, "bye bar");
    }

    @GET
    @Path("/client")
    @Produces("application/json")
    public Bar client() {
        Bar obj = new Bar(1, "hello bar");
        return client.bar(obj);
    }
}
