import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SimpleApiTests {
    @Test
    public void getSingleUserFirstNameTest() {
        get("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .body("data.first_name", is("Janet"));
    }

    @Test
    public void getSingleUserNotFoundTest() {
        get("https://reqres.in/api/users/23")
                .then()
                .statusCode(404);
    }

    @Test
    public void createNewUserTest() {
        JSONObject requestParams = new JSONObject();
        requestParams
                .put("name", "hi")
                .put("job", "hello");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestParams.toString())
                .post("https://reqres.in/api/users");

        Assertions.assertEquals(response.getStatusCode(), 201);
        Assertions.assertEquals("hi", response.jsonPath().get("name"));
        Assertions.assertEquals("hello", response.jsonPath().get("job"));
    }

    @Test
    public void deleteUserTest() {
        given()
                .delete("https://reqres.in/api/users/2")
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
                .header("Content-Type", "application/json")
                .body(requestParams.toString())
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(200)
                .body("token", is(notNullValue()));
    }
}
