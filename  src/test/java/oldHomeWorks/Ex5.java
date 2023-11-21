package test.java.oldHomeWorks;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Ex5 {
    @Test
    public void ex5(){
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        ArrayList<LinkedHashMap<String, Object>> jsonPaths = response.get("messages");
        LinkedHashMap<String, Object> secondAnswer = jsonPaths.get(1);
        System.out.println(secondAnswer.get("message"));
    }
}