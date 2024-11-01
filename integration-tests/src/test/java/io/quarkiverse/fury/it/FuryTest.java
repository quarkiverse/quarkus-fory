package io.quarkiverse.fury.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FuryTest {

    @Test
    public void testRecord() {
        given()
                .when().get("/fury/record")
                .then()
                .statusCode(200)
                .body(is("true"));
    }

    @Test
    public void testPojo() {
        given()
                .when().get("/fury/pojo")
                .then()
                .statusCode(200)
                .body(is("true"));
    }
}
