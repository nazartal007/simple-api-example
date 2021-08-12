import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;

public class DemowebshopTests {
    String cookies;
    int numberItems;

    String baseUrl = "http://demowebshop.tricentis.com/";

    @Test
    @DisplayName("adding item in basket with API and check adding item through API and UI")
    public void DemowebshopTest() {
        step("get cookies with api", () -> {
            cookies =
                    when()
                            .get(baseUrl + "build-your-cheap-own-computer")
                            .then()
                            .statusCode(200)
                            .extract()
                            .cookie("Nop.customer");
        });

        step("get number items in basket and save with api", () -> {
            String stringNumberItems =
                    given()
                            .cookie("Nop.customer=" + cookies + ";")
                            .when()
                            .get(baseUrl + "build-your-cheap-own-computer")
                            .then()
                            .statusCode(200)
                            .extract().htmlPath().getString("'**'.findAll{it.@class == 'cart-qty'}")
                            .substring(1, 2);

            numberItems = Integer.parseInt(stringNumberItems);
        });

        step("add item in basket with api and check adding item", () -> {
            given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&product_attribute_72_3_20=57&addtocart_72.EnteredQuantity=1")
                    .cookie("Nop.customer=" + cookies + ";")
            .when()
                    .post(baseUrl + "addproducttocart/details/72/1")
            .then()
                    .statusCode(200)
                    .body("success", is(true))
                    .body("updatetopcartsectionhtml", is("(" + (numberItems + 1) + ")"))
                    .extract().asString();
        });

        step("open page with cookie and check adding item in ui", () -> {
            open(baseUrl + "build-your-cheap-own-computer");
            WebDriverRunner.getWebDriver().manage().addCookie(new Cookie("Nop.customer", cookies));
            refresh();
            $(".cart-qty").should(Condition.text(String.valueOf(numberItems + 1)));
        });
    }
}
