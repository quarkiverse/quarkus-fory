package io.quarkiverse.fory.it;

import static io.quarkiverse.fory.it.ForyResources.BAR_CLASS_ID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.apache.fory.Fory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ForyTest {

    @Test
    public void testRecord() {
        given().when().get("/fory/record").then().statusCode(200).body(is("true"));
    }

    @Test
    public void testPojo() {
        given().when().get("/fory/pojo").then().statusCode(200).body(is("true"));
    }

    @Test
    public void testThirdPartyBar() {
        given().when().get("/fory/third_party_bar").then().statusCode(200).body(is("true"));
    }

    @Test
    public void testForyStruct() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        Struct struct = Struct.create();
        Fory fory = Fory.builder().requireClassRegistration(false).withName("Fory" + System.nanoTime()).build();
        Response response = given().contentType("application/fory").body(fory.serialize(struct)).when()
                .post("/fory/struct").then().statusCode(200).contentType("application/fory").extract().response();
        byte[] result = response.body().asByteArray();
        Struct struct1 = (Struct) fory.deserialize(result);
        Assertions.assertEquals(struct1, struct);
    }

    @Test
    public void testForyBar() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Bar bar = new Bar(1, "hello bar");
        Fory fory = Fory.builder().requireClassRegistration(true).withName("Fory" + System.nanoTime()).build();
        fory.register(Bar.class, BAR_CLASS_ID);
        fory.registerSerializer(Bar.class, BarSerializer.class);

        Response response = given().contentType("application/fory").body(fory.serialize(bar)).when()
                .post("/fory/test").then().statusCode(200).contentType("application/fory").extract().response();

        byte[] result = response.body().asByteArray();
        Bar bar2 = (Bar) fory.deserialize(result);
        Assertions.assertEquals(bar2.f1(), 2);
        Assertions.assertEquals(bar2.f2(), "bye bar");
    }
}
