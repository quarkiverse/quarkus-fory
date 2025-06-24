package io.quarkiverse.fory.it;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/fory")
@RegisterRestClient
public interface Client {
    @POST
    @Path("/test")
    @Produces("application/fory")
    @Consumes("application/fory")
    Bar bar(Bar obj);
}
