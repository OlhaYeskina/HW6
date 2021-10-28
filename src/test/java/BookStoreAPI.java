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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsMapContaining.hasKey;


public class BookStoreAPI {

    User user = new User();
    AuthToken authToken = new AuthToken();

    @Test
    public void checkRestAssured() {
        getPreparedRequest()

                .when().get("BookStore/BookStoreV1BooksGet")
                .then().assertThat().statusCode(200).assertThat().body(Matchers.notNullValue())
                .assertThat().header("Content-Type", "text/html; charset=UTF-8");
    }

    @Test
    public void createUserSuccess() {
        //  JSONObject requestParams = new JSONObject();
//        requestParams.put("password", "Password!23");
//        requestParams.put("userName", "userName2");
        RequestSpecification body = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"userName\": \"userName13\", \"password\": \"Password!23\"}");
        ValidatableResponse response = body.when().post("Account/v1/User").then().assertThat().statusCode(406)
                .log().all();

//        getPreparedRequest()
//                .header("accept: application/json","Content-Type: application/json")
//                .body("{ \"userName\": \"userName2\", \"password\": \"Password!23\"}")
//                .when().post("/swagger/#/Account/AccountV1UserPost")
//                .then().assertThat().statusCode(200).assertThat().body(Matchers.notNullValue());
    }

    @Test
    public void checkUserExists() {
        getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"userName\": \"userName8\", \"password\": \"Password!23\"}")
                .when().post("Account/v1/User").then()
                .assertThat().statusCode(406)
                .assertThat().body("code", equalTo("1204"))
                .assertThat().body("message", equalTo("User exists!")).log().all();
    }

    @Test
    public void createNewUser() {
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"userName\": \"userName12\", \"password\": \"Password!23\"}")
                .when().post("Account/v1/User").then()
                .assertThat().statusCode(201)
                .assertThat().body("username", equalTo("userName12")).log().all();

        user.setUserId(response.extract().path("userID"));
//        System.out.println(user.getUserId());
    }

    @Test
    public void checkUserAuthorizedSuccess() {
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6InVzZXJOYW1lMTMiLCJwYXNzd29yZCI6IlBhc3N3b3JkITIzIiwiaWF0IjoxNjM1NDE5MTk5fQ.n1us2Bz_gNBb15go4E8AoSVzhjqQd_ZfM55Dt7C0uCU")
                .pathParam("UUID", "cfd71945-c131-467c-a368-ef4c7796afb8")
                .when().get("Account/v1/User/{UUID}")
                .then().assertThat().statusCode(200).log().all();
    }

    @Test
    public void generateTokenSuccess() {
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"userName\": \"userName12\", \"password\": \"Password!23\"}")
                .when().post("Account/v1/GenerateToken")
                .then().assertThat().statusCode(200)
                .assertThat().body("status", equalTo("Success")).log().all();
        authToken.setToken(response.extract().path("token"));
    }

    @Test
    public void AuthorizedSuccess() {
        ValidatableResponse response = getPreparedRequest()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6InVzZXJOYW1lMTMiLCJwYXNzd29yZCI6IlBhc3N3b3JkITIzIiwiaWF0IjoxNjM1NDE5MTk5fQ.n1us2Bz_gNBb15go4E8AoSVzhjqQd_ZfM55Dt7C0uCU")
                .body("{ \"userName\": \"userName13\", \"password\": \"Password!23\"}")
                .when().post("/Account/v1/Authorized")
                .then().assertThat().statusCode(200).log().all();
//                .assertThat().body("", equalTo(true)).log().all();
//        authToken.setToken(response.extract().path("token"));
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

}
