package io.quarkiverse.fury.it;

import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;

@Path("/fury")
@ApplicationScoped
public class FuryResources {
    @Inject
    BaseFury fury;

    @GET
    @Path("/record")
    public Boolean testSerializeFooRecord() {
        Foo foo1 = new Foo(10, "abc", List.of("str1", "str2"), Map.of("k1", 10L, "k2", 20L));
        Foo foo2 = (Foo) fury.deserialize(fury.serialize(foo1));

        return foo1.equals(foo2);
    }

    @GET
    @Path("/pojo")
    public Boolean testSerializePOJO() {
        Struct struct1 = Struct.create();
        Struct struct2 = (Struct) fury.deserialize(fury.serialize(struct1));

        return struct1.equals(struct2);
    }

}
