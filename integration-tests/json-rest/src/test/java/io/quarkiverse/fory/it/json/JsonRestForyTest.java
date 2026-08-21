package io.quarkiverse.fory.it.json;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class JsonRestForyTest {

    @Test
    public void testGetBar() {
        given().when().get("/fory/json/bar")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("f1", is(42), "f2", is("hello json"));
    }

    @Test
    public void testPostBar() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"f1\":1,\"f2\":\"hello bar\"}")
                .when().post("/fory/json/bar")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("f1", is(2), "f2", is("echo: hello bar"));
    }

    @Test
    public void testPostBarsPreservesGenericElementType() {
        given()
                .contentType(ContentType.JSON)
                .body("[{\"f1\":1,\"f2\":\"a\"},{\"f1\":2,\"f2\":\"b\"}]")
                .when().post("/fory/json/bars")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("f1", is(java.util.List.of(2, 3)), "f2", is(java.util.List.of("echo: a", "echo: b")));
    }
}
