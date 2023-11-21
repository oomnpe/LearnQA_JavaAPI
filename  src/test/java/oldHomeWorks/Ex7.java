package test.java.oldHomeWorks;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex7 {

    int count;

    @Test
    public void ex7() {
        count = 0;
        go("https://playground.learnqa.ru/api/long_redirect");
        System.out.println("\nКоличество редиректов " + count);
    }

    private void go(String link) {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(link)
                .andReturn();

        if (response.getStatusCode() == 200) {
            System.out.println("\nДошли до кода 200");
            return;
        }

        String location = String.valueOf(response.getHeaders().get("Location").getValue());
        System.out.println("Редирект на " + location);
        ++count;

        go(location);
    }
}