import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

public class DemowebshopTests {
    String cookies;
    int numberItems;

    @Test
    public void DemowebShopTest() {
        step("get cookies", () -> {
            cookies =
                    when()
                            .get("http://demowebshop.tricentis.com/build-your-cheap-own-computer")
                    .then()
                            .statusCode(200)
                            .extract()
                            .cookie("Nop.customer");
        });

        step("get number items in basket", () -> {
            String stringNumberItems =
                    given()
                            .cookie("Nop.customer=" + cookies + ";")
                    .when()
                            .get("http://demowebshop.tricentis.com/build-your-cheap-own-computer")
                    .then()
                            .statusCode(200)
                            .extract().htmlPath().getString("'**'.findAll{it.@class == 'cart-qty'}")
                            .substring(1, 2);
            numberItems = Integer.parseInt(stringNumberItems);
        });

        step("add item in basket", () -> {
            given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&product_attribute_72_3_20=57&addtocart_72.EnteredQuantity=1")
                    .cookie("Nop.customer=" + cookies + ";")
                    .when()
                    .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                    .then()
                    .statusCode(200)
                    .body("success", is(true))
                    .body("updatetopcartsectionhtml", is("(" + (numberItems + 1)+")"))
                    .extract().asString();
        });
    }
}
