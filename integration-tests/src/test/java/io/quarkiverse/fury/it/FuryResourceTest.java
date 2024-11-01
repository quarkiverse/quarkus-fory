package io.quarkiverse.fury.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FuryResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/fury")
                .then()
                .statusCode(200)
                .body(is("Hello fury"));
    }
}
