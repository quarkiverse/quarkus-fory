package io.quarkiverse.fury.it.classic;

import static io.quarkiverse.fury.it.FuryResources.BAR_CLASS_ID;

import java.net.URL;

import org.apache.fury.BaseFury;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkiverse.fury.ClientFurySerializer;
import io.quarkiverse.fury.it.Bar;
import io.quarkiverse.fury.it.BarSerializer;
import io.quarkiverse.fury.it.Client;
import io.quarkiverse.fury.it.FuryTest;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ClassicRestFuryTest extends FuryTest {
    @TestHTTPResource
    URL url;

    @Test
    public void testRestClient() {
        Client client = RestClientBuilder.newBuilder().baseUrl(url).build(Client.class);
        BaseFury fury = ClientFurySerializer.getFuryInstance();
        fury.register(Bar.class, BAR_CLASS_ID);
        fury.registerSerializer(Bar.class, BarSerializer.class);

        Bar obj = new Bar(1, "hello bar");
        Bar bar2 = client.bar(obj);
        Assertions.assertEquals(bar2.f1(), 2);
        Assertions.assertEquals(bar2.f2(), "bye bar");
    }
}
