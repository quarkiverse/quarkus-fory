package io.quarkiverse.fory.it.classic;

import static io.quarkiverse.fory.it.ForyResources.BAR_CLASS_ID;

import java.net.URL;

import org.apache.fory.BaseFory;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkiverse.fory.ClientForySerializer;
import io.quarkiverse.fory.it.Bar;
import io.quarkiverse.fory.it.BarSerializer;
import io.quarkiverse.fory.it.Client;
import io.quarkiverse.fory.it.ForyTest;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ClassicRestForyTest extends ForyTest {
    @TestHTTPResource
    URL url;

    @Test
    public void testRestClient() {
        Client client = RestClientBuilder.newBuilder().baseUrl(url).build(Client.class);
        BaseFory fory = ClientForySerializer.getForyInstance();
        fory.register(Bar.class, BAR_CLASS_ID);
        fory.registerSerializer(Bar.class, BarSerializer.class);

        Bar obj = new Bar(1, "hello bar");
        Bar bar2 = client.bar(obj);
        Assertions.assertEquals(bar2.f1(), 2);
        Assertions.assertEquals(bar2.f2(), "bye bar");
    }
}
