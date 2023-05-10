package com.github.egosteva.tests;

import com.codeborne.selenide.Configuration;
import com.github.egosteva.models.CreateTestCaseBody;
import com.github.egosteva.models.CreateTestCaseResponse;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateTestCaseTests {

    static String login = "allure8",
            password = "allure8",
            projectId = "2257";

    @BeforeAll
    static void setUp() {
        Configuration.baseUrl = "https://allure.autotests.cloud";
        //       Configuration.holdBrowserOpen = true;
        Configuration.pageLoadStrategy = "eager";
        RestAssured.baseURI = "https://allure.autotests.cloud";
    }

    @Test
    void createWithUiOnlyTest() {
        Faker faker = new Faker();
        String testCaseName = faker.name().fullName();

        step("Authorize", () -> {
            open("/");
            $(byName("username")).setValue(login);
            $(byName("password")).setValue(password);
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
        Faker faker = new Faker();
        String testCaseName = faker.name().fullName();

//        step("Authorize", () -> {
//            open("/");
//            $(byName("username")).setValue(login);
//            $(byName("password")).setValue(password);
//            $("button[type='submit']").click();
//        });
//        step("Go to project", () -> {
//            open("/project/2220/test-cases");
//        });
        step("Authorize");
        //    step("Create testcase", () -> {
        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);

        //         given()
        //                 .log().all()
        //                 .header("X-XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce")
        //                 .cookies("XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce",
        //                         "ALLURE_TESTOPS_SESSION", "6f3e0152-f44f-4ea1-8b6c-e0d9b3c3c905")
        //                 .contentType("application/json;charset=UTF-8")
        //                 .body(testCaseBody)
        //                 .queryParam("projectId", projectId)
        //                 .when()
        //                 .post("/api/rs/testcasetree/leaf")
        //                 .then()
        //                 .log().status()
        //                 .log().body()
        //                 .statusCode(200)
        //                 .body("statusName", is("Draft"))
        //                 .body("name", is(testCaseName));
        //     });

        CreateTestCaseResponse createTestCaseResponse = step("Create testcase", () ->
                        given()
                                .log().all()
                                .header("X-XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce")
                                .cookies("XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce",
                                        "ALLURE_TESTOPS_SESSION", "6f3e0152-f44f-4ea1-8b6c-e0d9b3c3c905")
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
                //         .body("statusName", is("Draft"))
                //          .body("name", is(testCaseName));
        );

        step("Check test case name", () ->
                assertThat(createTestCaseResponse.getName()).isEqualTo(testCaseName));
    }


    @Test
    void createWitApiAndUiTest() {
        Faker faker = new Faker();
        String testCaseName = faker.name().fullName();
        step("Authorize");

        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponse createTestCaseResponse = step("Create testcase", () ->
                        given()
                                .log().all()
                                .header("X-XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce")
                                .cookies("XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce",
                                        "ALLURE_TESTOPS_SESSION", "6f3e0152-f44f-4ea1-8b6c-e0d9b3c3c905")
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
                //         .body("statusName", is("Draft"))
                //          .body("name", is(testCaseName));
        );

        step("Check test case name", () -> {
            open("/favicon.ico");
            //            assertThat(createTestCaseResponse.getName()).isEqualTo(testCaseName));
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", "6f3e0152-f44f-4ea1-8b6c-e0d9b3c3c905");
            getWebDriver().manage().addCookie(authorizationCookie);

            Integer testCaseId = createTestCaseResponse.getId();

            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
                 open(testCaseUrl);

            $(".TestCaseLayout__name").shouldHave(text(testCaseName));
        });
    }

    @Test
    void createWitApiAndUiExtendedTest() {
        Faker faker = new Faker();
        String testCaseName = faker.name().fullName();
        step("Authorize");

        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponse createTestCaseResponse = step("Create testcase", () ->
                        given()
                                .log().all()
                                .header("X-XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce")
                                .cookies("XSRF-TOKEN", "f6cce772-d46f-4b79-b9bf-9c598bb4cbce",
                                        "ALLURE_TESTOPS_SESSION", "6f3e0152-f44f-4ea1-8b6c-e0d9b3c3c905")
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
                //         .body("statusName", is("Draft"))
                //          .body("name", is(testCaseName));
        );

        step("Check test case name", () -> {
            open("/favicon.ico");
            //            assertThat(createTestCaseResponse.getName()).isEqualTo(testCaseName));
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", "6f3e0152-f44f-4ea1-8b6c-e0d9b3c3c905");
            getWebDriver().manage().addCookie(authorizationCookie);

            Integer testCaseId = createTestCaseResponse.getId();

            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
            open(testCaseUrl);

            $(".TestCaseLayout__name").shouldHave(text(testCaseName));
        });
    }
}
