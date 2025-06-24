package io.quarkiverse.fory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import org.apache.fory.BaseFory;
import org.apache.fory.Fory;

@Provider
@Consumes({ "application/fory", "application/*+fory" })
@Produces({ "application/fory", "application/*+fory" })
public class ClientForySerializer extends ForySerializer {
    static private BaseFory fory;

    public ClientForySerializer() {
        if (fory == null) {
            fory = Fory.builder().requireClassRegistration(true).withName("Fory" + System.nanoTime()).build();
        }
    }

    public static BaseFory getForyInstance() {
        return fory;
    }

    @Override
    public BaseFory getFory() {
        return super.getFory() == null ? fory : super.getFory();
    }
}
