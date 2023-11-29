package test.java.tests;

import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import test.java.lib.ApiCoreRequests;
import test.java.lib.Assertions;
import test.java.lib.BaseTestCase;
import test.java.lib.DataGenerator;

import java.util.HashMap;
import java.util.Map;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Epic("Тестирование REST API")
    @Feature("Удаление")
    @DisplayName("Попытка удалить пользователя по ID 2")
    @Owner("Иванов")
    public void tryDelUserWithId2() {

        //LOGIN & AUTH
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");

        apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/auth", header, cookie);

        //TRY TO DEL USER
        Response responseDelUser = apiCoreRequests.makeDeleteRequest("https://playground.learnqa.ru/api/user/2",
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseTextEquals(responseDelUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Удаление")
    @DisplayName("Тест на удаление пользователя")
    @Issue("https://jira..ru/browse/XXX-26223")
    public void delUserTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestGetJson("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.getString("id");

        //LOGIN & AUTH
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");

        apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/auth", header, cookie);

        //DEL USER
        Response responseDelUser = apiCoreRequests.makeDeleteRequest("https://playground.learnqa.ru/api/user/2",
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Удаление")
    @DisplayName("Тест на попытку удаления пользователя, будучи авторизованными другим пользователем")
    @Flaky
    public void tryDelUserWithAnotherUserAuthorization() {
        //GENERATE USER FOR DEL
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestGetJson("https://playground.learnqa.ru/api/user/", userData);
        String userForDelId = responseCreateAuth.getString("id");
        String userForDelUsername = userData.get("username");

        //GENERATE USER FOR LOGIN
        Map<String, String> authData = new HashMap<>();
        userData = DataGenerator.getRegistrationData();
        responseCreateAuth = apiCoreRequests.makePostRequestGetJson("https://playground.learnqa.ru/api/user/", userData);

        //LOGIN & AUTH WITH ANOTHER USER
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");

        apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/auth", header, cookie);

        //TRY EDIT WITH AUTH ANOTHER USER
        Response responseDelUser = apiCoreRequests.makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userForDelId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        responseDelUser.prettyPrint();

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userForDelId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertJsonByName(responseUserData, "username", userForDelUsername);
    }
}
