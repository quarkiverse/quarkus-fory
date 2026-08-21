package io.quarkiverse.fory.it.json;

import java.util.List;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkiverse.fory.it.Bar;

@Path("/fory/json")
public class JsonForyResources {

    @Inject
    @RestClient
    Instance<JsonClient> client;

    /** Round trips through the REST client, exercising the client-side JSON reader/writer. */
    @GET
    @Path("/client")
    @Produces(MediaType.APPLICATION_JSON)
    public Bar client() {
        return client.get().bar(new Bar(1, "hello bar"));
    }

    /** Uses a type with no {@code @ForySerialization} annotation. */
    @GET
    @Path("/plain")
    @Produces(MediaType.APPLICATION_JSON)
    public Plain getPlain() {
        return new Plain(7, "no annotation");
    }

    @POST
    @Path("/plain")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Plain postPlain(Plain p) {
        return new Plain(p.n() + 1, "echo: " + p.s());
    }

    @GET
    @Path("/bar")
    @Produces(MediaType.APPLICATION_JSON)
    public Bar getBar() {
        return new Bar(42, "hello json");
    }

    @POST
    @Path("/bars")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bar> postBars(List<Bar> bars) {
        return bars.stream().map(b -> new Bar(b.f1() + 1, "echo: " + b.f2())).toList();
    }

    @POST
    @Path("/bar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Bar postBar(Bar obj) {
        return new Bar(obj.f1() + 1, "echo: " + obj.f2());
    }
}
