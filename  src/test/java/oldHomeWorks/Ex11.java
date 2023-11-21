package test.java.oldHomeWorks;

import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Ex11 {

    @Test
    public void e11() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> respCookies = response.getCookies();

        assertEquals("hw_value", respCookies.get("HomeWork"), "Bad answer");
    }
}