import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import org.apache.tools.ant.taskdefs.condition.Matches;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsMapContaining.hasKey;


public class BookStoreAPI {

    static HashMap<String,String> userMap = new HashMap<>();


    @Test
    public void checkRestAssured() {
        getPreparedRequest()

                .when().get("BookStore/BookStoreV1BooksGet")
                .then().assertThat().statusCode(200).assertThat().body(Matchers.notNullValue())
                .assertThat().header("Content-Type", "text/html; charset=UTF-8");
    }

//    @Test
//    public void createUserSuccess() {
//        RequestSpecification body = getPreparedRequest()
//                .header("accept", "application/json")
//                .header("Content-Type", "application/json")
//                .body("{ \"userName\": \"userName13\", \"password\": \"Password!23\"}");
//        ValidatableResponse response = body.when().post("Account/v1/User").then().assertThat().statusCode(406)
//                .log().all();
//
////        getPreparedRequest()
////                .header("accept: application/json","Content-Type: application/json")
////                .body("{ \"userName\": \"userName2\", \"password\": \"Password!23\"}")
////                .when().post("/swagger/#/Account/AccountV1UserPost")
////                .then().assertThat().statusCode(200).assertThat().body(Matchers.notNullValue());
//    }

    @Test (priority = 2)
    public void checkUserExists() {
        getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"userName\": \""+ userMap.get("username")+"\", \"password\": \"Password!23\"}")
                .when().post("Account/v1/User").then()
                .assertThat().statusCode(406)
                .assertThat().body("code", equalTo("1204"))
                .assertThat().body("message", equalTo("User exists!")).log().all();
    }

    @Test (priority = 1)
    public void createNewUser() {

        String userName = givenRandomString();
        userMap.put("username", userName);
        userMap.put("password","Password!23");
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"userName\": \""+ userMap.get("username")+"\", \"password\": \"Password!23\"}")
                .when().post("Account/v1/User").then()
                .assertThat().statusCode(201)
                .assertThat().body("username", equalTo(userName)).log().all();


        userMap.put("UUID", response.extract().path("userID"));

               // setUserId(response.extract().path("userID"));
//        System.out.println(user.getUserId());
    }


    @Test(priority = 5)
    public void checkGetUserAuthorized() {
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+ userMap.get("token"))
                .pathParam("UUID", userMap.get("UUID"))
                .when().get("Account/v1/User/{UUID}")
                .then().assertThat().statusCode(200).log().all();
    }

    @Test(priority = 3)
    public void generateTokenSuccess() {
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"userName\": \""+ userMap.get("username")+"\", \"password\": \"Password!23\"}")
                .when().post("Account/v1/GenerateToken")
                .then().assertThat().statusCode(200)
                .assertThat().body("status", equalTo("Success")).log().all();
        userMap.put("token",(response.extract().path("token")));
    }

    @Test (priority = 4)
    public void checkAuthorizedSuccess() {
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+ userMap.get("token"))
                .body("{ \"userName\": \""+ userMap.get("username")+"\", \"password\": \"Password!23\"}")
                .when().post("/Account/v1/Authorized")
                .then().assertThat().statusCode(200).log().all();
    }


    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder().setBaseUri("https://demoqa.com/").setConfig(getConfig()).build();
    }

    public static RestAssuredConfig getConfig() {
        return RestAssuredConfig.newConfig();
    }

    public static RequestSpecification getPreparedRequest() {
        return given().spec(getRequestSpec()).log().all();
    }

    public static void main(String[] args) {
        System.out.println(userMap);
    }

    public static String givenRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

}
