package test.java.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runners.MethodSorters;
import test.java.lib.ApiCoreRequests;
import test.java.lib.Assertions;
import test.java.lib.BaseTestCase;
import test.java.lib.DataGenerator;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Epic("Тестирование REST API")
    @Feature("Регистрация")
    @DisplayName("Регистрация пользователя с существующим email")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Epic("Тестирование REST API")
    @Feature("Регистрация")
    @DisplayName("Регистрация пользователя")
    @Severity(BLOCKER)
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    /**
     * Создание пользователя с некорректным email - без символа @
     */
    @Test
    @Epic("Тестирование REST API")
    @Feature("Регистрация")
    @DisplayName("Регистрация пользователя с некорректным email")
    public void testCreateUserWithIncorrectEmail() {
        String email = "vinkotovexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    /**
     * Создание пользователя без указания одного из полей
     */
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    @Epic("Тестирование REST API")
    @Feature("Регистрация")
    @DisplayName("Регистрация пользователя  с отсутствующими сведениями")
    public void testCreateUserWithoutOneField(String missingField) {
        Map<String, String> userData = DataGenerator.getRegistrationDataWithoutOneField(missingField);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + missingField);
    }

    /**
     * Создание пользователя с очень коротким именем в один символ
     */
    @Test
    @Epic("Тестирование REST API")
    @Feature("Регистрация")
    @DisplayName("Регистрация пользователя с очень коротким именем")
    public void testCreateUserWithShortName() {
        String userName = "v";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", userName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    /**
     * Создание пользователя с очень длинным именем - длиннее 250 символов
     */
    @Test
    @Epic("Тестирование REST API")
    @Feature("Регистрация")
    @DisplayName("Регистрация пользователя с очень длинным именем")
    @Description("В данном тесте регистрируем пользователя с очень длинным именем")
    public void testCreateUserWithLongName() {
        String userName = DataGenerator.generateString("qwelkjoiu", 251);

        Map<String, String> userData = new HashMap<>();
        userData.put("username", userName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }
}
