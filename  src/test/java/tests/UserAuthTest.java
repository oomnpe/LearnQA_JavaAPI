package test.java.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import test.java.lib.ApiCoreRequests;
import test.java.lib.BaseTestCase;
import org.junit.FixMethodOrder;

import java.util.HashMap;
import java.util.Map;

import test.java.lib.Assertions;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Авторизация")
    @DisplayName("Тест на авторизацию пользователя")
    public void testAuthUser() {

        Response responseCheckAuth = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/auth", this.header, this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }
}
