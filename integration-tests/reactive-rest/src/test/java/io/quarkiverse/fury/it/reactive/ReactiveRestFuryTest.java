package io.quarkiverse.fury.it.reactive;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkiverse.fury.it.FuryTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ReactiveRestFuryTest extends FuryTest {
    @Test
    public void testClient() {
        given().when().get("/fury/client").then().statusCode(200).body("f1", is(2), "f2", is("bye bar"));
    }
}
