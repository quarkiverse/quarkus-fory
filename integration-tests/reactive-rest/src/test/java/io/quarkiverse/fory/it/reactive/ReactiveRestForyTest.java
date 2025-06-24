package io.quarkiverse.fory.it.reactive;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkiverse.fory.it.ForyTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ReactiveRestForyTest extends ForyTest {
    @Test
    public void testClient() {
        given().when().get("/fory/client").then().statusCode(200).body("f1", is(2), "f2", is("bye bar"));
    }
}
