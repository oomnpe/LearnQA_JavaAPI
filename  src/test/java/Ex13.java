package test.java;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Ex13 {

    @ParameterizedTest
    @ValueSource(strings = {"Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
    })
    public void e13(String userAgent) {
        Header header = new Header("User-Agent", userAgent);

        RequestSpecification spec = RestAssured.given();
        spec.header(header);
        spec.baseUri("https://playground.learnqa.ru/ajax/api/user_agent_check");
        JsonPath resp = spec.get().jsonPath();
        resp.prettyPrint();

        if (userAgent.equals("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")) {
            assertEquals("Mobile", resp.get("platform"), "the value platform is not equal to the expected value");
            assertEquals("No", resp.get("browser"), "the value browser is not equal to the expected value");
            assertEquals("Android", resp.get("device"), "the value device is not equal to the expected value");
        }
        if (userAgent.equals("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1")) {
            assertEquals("Mobile", resp.get("platform"), "the value platform is not equal to the expected value");
            assertEquals("Chrome", resp.get("browser"), "the value browser is not equal to the expected value");
            assertEquals("iOS", resp.get("device"), "the value device is not equal to the expected value");
        }
        if (userAgent.equals("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")) {
            assertEquals("Googlebot", resp.get("platform"), "the value platform is not equal to the expected value");
            assertEquals("Unknown", resp.get("browser"), "the value browser is not equal to the expected value");
            assertEquals("Unknown", resp.get("device"), "the value device is not equal to the expected value");
        }
        if (userAgent.equals("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0")) {
            assertEquals("Web", resp.get("platform"), "the value platform is not equal to the expected value");
            assertEquals("Chrome", resp.get("browser"), "the value browser is not equal to the expected value");
            assertEquals("No", resp.get("device"), "the value device is not equal to the expected value");
        }
        if (userAgent.equals("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")) {
            assertEquals("Mobile", resp.get("platform"), "the value platform is not equal to the expected value");
            assertEquals("No", resp.get("browser"), "the value browser is not equal to the expected value");
            assertEquals("iPhone", resp.get("device"), "the value device is not equal to the expected value");
        }
    }
}