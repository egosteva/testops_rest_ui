package com.github.egosteva.tests;

import com.github.egosteva.models.CreateTestCaseBodyModel;
import com.github.egosteva.models.CreateTestCaseResponseModel;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.github.egosteva.specifications.Specifications.requestSpec;
import static com.github.egosteva.specifications.Specifications.responseSpec;
import static com.github.egosteva.tests.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateTestCaseTests extends TestBase {
    Faker faker = new Faker();

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
        CreateTestCaseBodyModel testCaseBody = new CreateTestCaseBodyModel();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(testCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        step("Check test case name", () ->
                assertThat(createTestCaseResponse.getName()).isEqualTo(testCaseName));
    }

    @Test
    void createWitApiAndUiTest() {
        String testCaseName = faker.name().fullName();
        step("Authorize");

        CreateTestCaseBodyModel testCaseBody = new CreateTestCaseBodyModel();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(testCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

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

        CreateTestCaseBodyModel testCaseBody = new CreateTestCaseBodyModel();
        testCaseBody.setName(testCaseName);

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(testCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        Integer testCaseId = createTestCaseResponse.getId();

        step("Check test case name", () -> {
            open("/favicon.ico");
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", allureTestopsSession);
            getWebDriver().manage().addCookie(authorizationCookie);


            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
            open(testCaseUrl);

            $(".TestCaseLayout__name").shouldHave(text(testCaseName));
        });
    }
}