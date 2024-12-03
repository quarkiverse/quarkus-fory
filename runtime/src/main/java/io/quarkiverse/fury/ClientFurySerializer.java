package io.quarkiverse.fury;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;

@Provider
@Consumes({ "application/fury", "application/*+fury" })
@Produces({ "application/fury", "application/*+fury" })
public class ClientFurySerializer extends FurySerializer {
    static private BaseFury fury;

    public ClientFurySerializer() {
        if (fury == null) {
            fury = Fury.builder().requireClassRegistration(true).withName("Fury" + System.nanoTime()).build();
        }
    }

    public static BaseFury getFuryInstance() {
        return fury;
    }

    @Override
    public BaseFury getFury() {
        return super.getFury() == null ? fury : super.getFury();
    }
}
