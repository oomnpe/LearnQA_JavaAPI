package test.java.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
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
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Epic("Тестирование REST API")
    @Feature("Редактирование")
    @DisplayName("Редактирование нового пользователя")
    public void testEditJustCreatedTest() {
        //GenerateUser
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //get

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();


        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Редактирование")
    @DisplayName("Попытка изменения данных пользователя без авторизации")
    public void testEditWithoutAuthorization() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestGetJson("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.getString("id");

        //TRY EDIT WITHOUT LOGIN
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId, editData);

        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("username"));
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Редактирование")
    @DisplayName("Попытка изменения данных пользователя будучи авторизованными другим пользователем")
    public void testEditAnotherUserAuthorization() {
        //GENERATE USER FOR CHANGE
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestGetJson("https://playground.learnqa.ru/api/user/", userData);
        String userForChangeId = responseCreateAuth.getString("id");
        String userForChangeUsername = userData.get("username");

        String cookieForChangeUser;
        String headerForChangeUser;

        //LOGIN & AUTH WITH CHANGE USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        cookieForChangeUser = this.getCookie(responseGetAuth, "auth_sid");
        headerForChangeUser = this.getHeader(responseGetAuth, "x-csrf-token");

        //GENERATE USER FOR LOGIN
        userData = DataGenerator.getRegistrationData();
        apiCoreRequests.makePostRequestGetJson("https://playground.learnqa.ru/api/user/", userData);

        //LOGIN & AUTH WITH ANOTHER USER
        authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");

        apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/auth", header, cookie);

        //TRY EDIT WITH AUTH ANOTHER USER
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userForChangeId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userForChangeId, headerForChangeUser, cookieForChangeUser);
        Assertions.assertJsonByName(responseUserData, "firstName", userForChangeUsername);
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Редактирование")
    @DisplayName("Попытка изменения email пользователя будучи авторизованными тем же пользователем, на новый email без символа @")
    public void testEditWithIncorrectEmail() {
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

        //TRY EDIT WITH AUTH INCORRECT EMAIL
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "qwerty&mail.ru");

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData);

        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Редактирование")
    @DisplayName("Попытка изменения firstName пользователя, будучи авторизованными тем же пользователем, на очень короткое значение в один символ")
    public void testEditWithShortFirstName() {
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

        //TRY EDIT WITH SHORT FIRSTNAME
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "a");

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData);

        Assertions.assertJsonHasField(responseEditUser, "error");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
