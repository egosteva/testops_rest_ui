package com.github.egosteva.tests;

import com.github.egosteva.models.*;
import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.github.egosteva.specifications.Specifications.requestSpec;
import static com.github.egosteva.specifications.Specifications.responseSpec;
import static com.github.egosteva.tests.TestData.allureTestopsSession;
import static com.github.egosteva.tests.TestData.projectId;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;

@Feature("Update TestOps test case using REST and UI")
@DisplayName("Update TestOps test case using REST and UI")
public class UpdateTestCaseTests extends TestBase {
    Faker faker = new Faker();

    String testCaseNameInitial = faker.artist().name();
    String testCaseNameUpdated = faker.book().author();
    String testCaseDescriptionInitial = faker.address().fullAddress();
    String testCaseDescriptionUpdated = faker.color().name();
    String stepNameInitial = faker.book().title();
    String stepNameUpdated = faker.book().genre();

    @Test
    @Story("Update test case")
    @Owner("egosteva")
    @DisplayName("Edit name and description of test case")
    @Tag("rest_ui")
    void updateTestCaseNameAndDescriptionTest() {
        CreateTestCaseBodyModel createTestCaseBody = new CreateTestCaseBodyModel();
        createTestCaseBody.setName(testCaseNameInitial);

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(createTestCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        Integer testCaseId = createTestCaseResponse.getId();

        AddDescriptionBodyModel addDescriptionBody = new AddDescriptionBodyModel();
        addDescriptionBody.setId(testCaseId);
        addDescriptionBody.setDescription(testCaseDescriptionInitial);
        addDescriptionBody.setDescriptionHtml(null);

        AddDescriptionResponseModel addDescriptionResponse = step("Add description to testcase", () ->
            given(requestSpec)
                    .body(addDescriptionBody)
                    .when()
                    .patch("/testcase/" + testCaseId)
                    .then()
                    .spec(responseSpec)
                    .extract().as(AddDescriptionResponseModel.class));

        step("Open test case page", () -> {
            open("/favicon.ico");
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", allureTestopsSession);
            getWebDriver().manage().addCookie(authorizationCookie);
            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
            open(testCaseUrl);
        });

        step("Check test case name", () ->
                $(".TestCaseLayout__name").shouldHave(text(testCaseNameInitial)));

        step("Check description", () ->
                $("[data-testid=section__description]").shouldHave(text(testCaseDescriptionInitial)));

        step("Update test case name", () -> {
            $(".Menu__trigger").click();
            $(byText("Rename test case")).click();
            $("[placeholder='Enter name']").clear();
            $("[placeholder='Enter name']").setValue(testCaseNameUpdated);
            $(byText("Submit")).click();
        });

        step("Check updated test case name", () ->
                $(".TestCaseLayout__name").shouldHave(text(testCaseNameUpdated)));

        step("Update description", () -> {
            $("[data-testid=section__description]").$("[data-testid=button__edit_section]").click();
            $("[data-testid=section__description]").$(".MarkdownTextarea__edit").clear();
            $("[data-testid=section__description]").$(".MarkdownTextarea__edit").setValue(testCaseDescriptionUpdated);
            $(byText("Submit")).click();
        });

        step("Check updated description", () ->
                $("[data-testid=section__description]").shouldHave(text(testCaseDescriptionUpdated)));
    }

    @Test
    @Story("Update test case")
    @Owner("egosteva")
    @DisplayName("Add step to test case")
    @Tag("rest_ui")
    void addStepToTestCaseTest() {
        CreateTestCaseBodyModel createTestCaseBody = new CreateTestCaseBodyModel();
        createTestCaseBody.setName(testCaseNameInitial);

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(createTestCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        Integer testCaseId = createTestCaseResponse.getId();

        step("Open test case page", () -> {
            open("/favicon.ico");
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", allureTestopsSession);
            getWebDriver().manage().addCookie(authorizationCookie);
            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
            open(testCaseUrl);
        });

        step("Check test case name", () ->
                $(".TestCaseLayout__name").shouldHave(text(testCaseNameInitial)));

        step("Add step to test case", () -> {
            $("[data-testid=section__scenario]").$("[data-testid=button__edit_section]").click();
            $(".TestCaseScenarioStepEdit__textarea").setValue(stepNameInitial);
            $(byText("Submit")).click();
        });

        step("Check step name", () -> {
            $("TestCaseScenarioStep__name").shouldHave(text(stepNameInitial));
        });
    }

    @Test
    @Story("Update test case")
    @Owner("egosteva")
    @DisplayName("Update step of test case")
    @Tag("rest_ui")
    void updateStepOfTestCaseTest() {
        CreateTestCaseBodyModel createTestCaseBody = new CreateTestCaseBodyModel();
        createTestCaseBody.setName(testCaseNameInitial);

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(createTestCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        Integer testCaseId = createTestCaseResponse.getId();

        AddStepBodyModel.ListStepsData step = new AddStepBodyModel.ListStepsData();
        step.setName(stepNameInitial);
        step.setSpacing("");

        AddStepBodyModel addStepBody = new AddStepBodyModel();
        List<AddStepBodyModel.ListStepsData> listSteps = new ArrayList<>();
        listSteps.add(step);
        addStepBody.setSteps(listSteps);
        addStepBody.setWorkPath(0);

        AddStepResponseModel addStepResponse = step("Add step to testcase", () ->
                given(requestSpec)
                        .body(addStepBody)
                        .when()
                        .post("/testcase/" + testCaseId + "/scenario")
                        .then()
                        .spec(responseSpec)
                        .extract().as(AddStepResponseModel.class));

        step("Open test case page", () -> {
            open("/favicon.ico");
            Cookie authorizationCookie = new Cookie(
                    "ALLURE_TESTOPS_SESSION", allureTestopsSession);
            getWebDriver().manage().addCookie(authorizationCookie);
            String testCaseUrl = format("/project/%s/test-cases/%s", projectId, testCaseId);
            open(testCaseUrl);
        });

        step("Check test case name", () ->
                $(".TestCaseLayout__name").shouldHave(text(testCaseNameInitial)));

        step("Check step name", () -> {
            $(".TestCaseScenarioStep__name").shouldHave(text(stepNameInitial));
        });

        step("Edit step of test case", () -> {
            $("[data-testid=section__scenario]").$("[data-testid=button__edit_section]").click();
            $(".TestCaseScenarioStepEdit__textarea").clear();
            $(".TestCaseScenarioStepEdit__textarea").setValue(stepNameUpdated);
            $(byText("Submit")).click();
        });

        step("Check updated step name", () -> {
            $(".TestCaseScenarioStep__name").shouldHave(text(stepNameUpdated));
        });
    }
}