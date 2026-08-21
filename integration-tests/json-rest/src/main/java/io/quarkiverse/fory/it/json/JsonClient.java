package io.quarkiverse.fory.it.json;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkiverse.fory.it.Bar;

@Path("/fory/json")
@RegisterRestClient
public interface JsonClient {

    @POST
    @Path("/bar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Bar bar(Bar obj);
}
