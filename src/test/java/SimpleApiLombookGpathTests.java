import lombookModels.User;
import lombookModels.UserData;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleApiLombookGpathTests {
    @Test
    public void getSingleUserFirstNameTest() {
        UserData data =
                given()
                    .spec(Specs.request)
                .when()
                    .get("/users/2")
                .then()
                    .spec(Specs.responseSpec)
                    .extract().as(UserData.class);

        assertEquals("Janet", data.getData().getFirstName());
        assertEquals("Weaver", data.getData().getLastName());
    }

    @Test
    public void getSingleUserNotFoundTest() {
        given()
            .spec(Specs.request)
        .when()
            .get("/users/23")
        .then()
            .statusCode(404);
    }

    @Test
    public void createNewUserTest() {
        JSONObject requestParams = new JSONObject();
        requestParams
                .put("name", "hi")
                .put("job", "hello");

        User user =
                given()
                    .header("Content-Type", "application/json")
                    .spec(Specs.request)
                .when()
                    .body(requestParams.toString())
                    .post("/users")
                .then()
                    .statusCode(201)
                    .extract().as(User.class);

        assertEquals("hi", user.getName());
        assertEquals("hello", user.getJob());
    }

    @Test
    public void deleteUserTest() {
        given()
            .spec(Specs.request)
        .when()
            .delete("users/2")
        .then()
            .statusCode(204);
    }

    @Test
    public void loginUserTest() {
        JSONObject requestParams = new JSONObject();
        requestParams
                .put("email", "eve.holt@reqres.in")
                .put("password", "cityslicka");

        given()
            .spec(Specs.request)
        .when()
            .header("Content-Type", "application/json")
            .body(requestParams.toString())
            .post("/login")
        .then()
            .spec(Specs.responseSpec)
            .body("token", is(notNullValue()));
    }

    @Test
    void testWithGpath(){
        given()
            .spec(Specs.request)
        .when()
            .get("/users?delay=3")
        .then()
            .spec(Specs.responseSpec)
            .body("data.email.findAll{it}", hasItem("janet.weaver@reqres.in"));
    }

    @Test
    void testWithGpath2(){
        given()
                .spec(Specs.request)
            .when()
                .get("/unknown")
            .then()
                .spec(Specs.responseSpec)
                .body("data.name.findAll{it}", hasItem("fuchsia rose"));
    }
}
