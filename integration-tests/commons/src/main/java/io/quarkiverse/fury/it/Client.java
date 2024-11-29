package io.quarkiverse.fury.it;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/fury")
@RegisterRestClient
public interface Client {
    @POST
    @Path("/test")
    @Produces("application/fury")
    @Consumes("application/fury")
    Bar bar(Bar obj);
}
