package io.quarkiverse.fury.it;

import static io.quarkiverse.fury.it.FuryResources.BAR_CLASS_ID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.fury.Fury;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class FuryTest {

    @Test
    public void testRecord() {
        given().when().get("/fury/record").then().statusCode(200).body(is("true"));
    }

    @Test
    public void testPojo() {
        given().when().get("/fury/pojo").then().statusCode(200).body(is("true"));
    }

    @Test
    public void testThirdPartyBar() {
        given().when().get("/fury/third_party_bar").then().statusCode(200).body(is("true"));
    }

    @Test
    public void testFuryStruct() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        Struct struct = Struct.create();
        Fury fury = Fury.builder().requireClassRegistration(false).withName("Fury" + System.nanoTime()).build();
        Response response = given().contentType("application/fury").body(fury.serialize(struct)).when()
                .post("/fury/struct").then().statusCode(200).contentType("application/fury").extract().response();
        byte[] result = response.body().asByteArray();
        Struct struct1 = (Struct) fury.deserialize(result);
        Assertions.assertEquals(struct1, struct);
    }

    @Test
    public void testFuryBar() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Bar bar = new Bar(1, "hello bar");
        Fury fury = Fury.builder().requireClassRegistration(true).withName("Fury" + System.nanoTime()).build();
        fury.register(Bar.class, BAR_CLASS_ID);
        fury.registerSerializer(Bar.class, BarSerializer.class);

        Response response = given().contentType("application/fury").body(fury.serialize(bar)).when()
                .post("/fury/test").then().statusCode(200).contentType("application/fury").extract().response();

        byte[] result = response.body().asByteArray();
        Bar bar2 = (Bar) fury.deserialize(result);
        Assertions.assertEquals(bar2.f1(), 2);
        Assertions.assertEquals(bar2.f2(), "bye bar");
    }
}
