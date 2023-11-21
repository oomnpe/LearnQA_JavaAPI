package test.java.oldHomeWorks;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Ex12 {

    @Test
    public void e12() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        String header = response.getHeaders().get("x-secret-homework-header").getValue();

        assertEquals("Some secret value", header, "Bad answer");
    }
}