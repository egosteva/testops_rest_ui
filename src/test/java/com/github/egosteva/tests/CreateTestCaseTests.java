package com.github.egosteva.tests;

import com.github.egosteva.models.CreateTestCaseBody;
import com.github.egosteva.models.CreateTestCaseResponse;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateTestCaseTests extends TestBase {
    Faker faker = new Faker();
    String login = "allure8",
            password = "allure8",
            projectId = "2257",
            xsrfToken = "50011563-6a05-4b2e-95ca-344ef2831d85",
            allureTestopsSession = "613d00cc-9e4c-4415-bf09-011e9ddf1794";


    @Test
    void createWithUiOnlyTest() {
        String testCaseName = faker.name().fullName();

        step("Authorize", () -> {
            open("/");
            $("input[placeholder='Username']").setValue(login);
            $("input[placeholder='Password']").setValue(password);
            $("button[type='submit']").click();
            $("[data-testid=sidemenu]").shouldBe(visible);
        });
        step("Go to project", () -> {
            open("/project/" + projectId + "/test-cases");
        });
        step("Create test case", () -> {
            $("[data-testid=input__create_test_case]").setValue(testCaseName)
                    .pressEnter();
        });
        step("Check test case name", () -> {
            $(".LoadableTree__view").shouldHave(text(testCaseName));
        });
    }

    @Test
    void createWitApiOnlyTest() {
        String testCaseName = faker.name().fullName();

        step("Authorize");
        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponse createTestCaseResponse = step("Create testcase", () ->
                        given()
                                .log().all()
                                .header("X-XSRF-TOKEN", xsrfToken)
                                .cookies("XSRF-TOKEN", xsrfToken,
                                        "ALLURE_TESTOPS_SESSION", allureTestopsSession)
                                .contentType("application/json;charset=UTF-8")
                                .body(testCaseBody)
                                .queryParam("projectId", projectId)
                                .when()
                                .post("/api/rs/testcasetree/leaf")
                                .then()
                                .log().status()
                                .log().body()
                                .statusCode(200)
                                .extract().as(CreateTestCaseResponse.class)
        );

        step("Check test case name", () ->
                assertThat(createTestCaseResponse.getName()).isEqualTo(testCaseName));
    }

    @Test
    void createWitApiAndUiTest() {
        String testCaseName = faker.name().fullName();
        step("Authorize");

        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponse createTestCaseResponse = step("Create testcase", () ->
                        given()
                                .log().all()
                                .header("X-XSRF-TOKEN", xsrfToken)
                                .cookies("XSRF-TOKEN", xsrfToken,
                                        "ALLURE_TESTOPS_SESSION", allureTestopsSession)
                                .contentType("application/json;charset=UTF-8")
                                .body(testCaseBody)
                                .queryParam("projectId", projectId)
                                .when()
                                .post("/api/rs/testcasetree/leaf")
                                .then()
                                .log().status()
                                .log().body()
                                .statusCode(200)
                                .extract().as(CreateTestCaseResponse.class)
        );

        step("Check test case name", () -> {
            open("/favicon.ico");
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", allureTestopsSession);
            getWebDriver().manage().addCookie(authorizationCookie);
            Integer testCaseId = createTestCaseResponse.getId();
            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
                 open(testCaseUrl);

            $(".TestCaseLayout__name").shouldHave(text(testCaseName));
        });
    }

    @Test
    void createWitApiAndUiExtendedTest() {
        String testCaseName = faker.name().fullName();
        step("Authorize");

        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponse createTestCaseResponse = step("Create testcase", () ->
                        given()
                                .log().all()
                                .header("X-XSRF-TOKEN", xsrfToken)
                                .cookies("XSRF-TOKEN", xsrfToken,
                                        "ALLURE_TESTOPS_SESSION", allureTestopsSession)
                                .contentType("application/json;charset=UTF-8")
                                .body(testCaseBody)
                                .queryParam("projectId", projectId)
                                .when()
                                .post("/api/rs/testcasetree/leaf")
                                .then()
                                .log().status()
                                .log().body()
                                .statusCode(200)
                                .extract().as(CreateTestCaseResponse.class)
        );

        step("Check test case name", () -> {
            open("/favicon.ico");
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", allureTestopsSession);
            getWebDriver().manage().addCookie(authorizationCookie);

            Integer testCaseId = createTestCaseResponse.getId();

            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
            open(testCaseUrl);

            $(".TestCaseLayout__name").shouldHave(text(testCaseName));
        });
    }
}