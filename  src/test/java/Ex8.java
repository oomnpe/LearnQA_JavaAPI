package test.java;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ex8 {
    @Test
    public void ex8() {
        String token;
        JsonPath response = go(null);
        token = response.get("token");

        int waitSec = response.get("seconds");

        response = go(token);

        if (response.get("status").equals("Job is NOT ready")) {
            System.out.println("Проверен статус 'Job is NOT ready'");
        } else {
            System.err.println("Ошибка. Ожидалось 'Job is NOT ready'");
        }

        try {
            System.out.println("Ждем " + waitSec + " сек");
            Thread.sleep(waitSec * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = go(token);

        if (response.get("result") != null & response.get("status").equals("Job is ready")) {
            System.out.println("Ответ на запрос корректен");
        } else {
            System.err.println("ERROR!");
        }
    }

    private JsonPath go(String token) {
        JsonPath response;

        if (token == null) {
            response = RestAssured
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();
        } else {
            response = RestAssured
                    .given()
                    .queryParam("token", token)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();
        }
        response.prettyPrint();

        return response;
    }
}