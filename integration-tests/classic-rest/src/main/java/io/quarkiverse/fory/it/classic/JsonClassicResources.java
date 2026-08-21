package io.quarkiverse.fory.it.classic;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkiverse.fory.it.Bar;

@Path("/fory/json")
public class JsonClassicResources {

    @GET
    @Path("/bar")
    @Produces(MediaType.APPLICATION_JSON)
    public Bar getBar() {
        return new Bar(42, "hello json");
    }

    @POST
    @Path("/bar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Bar postBar(Bar obj) {
        return new Bar(obj.f1() + 1, "echo: " + obj.f2());
    }
}
