package com.github.egosteva.tests;

import com.github.egosteva.models.AddDescriptionBodyModel;
import com.github.egosteva.models.AddDescriptionResponseModel;
import com.github.egosteva.models.CreateTestCaseBodyModel;
import com.github.egosteva.models.CreateTestCaseResponseModel;
import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

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

@Feature("TestOps test using REST and UI")
@DisplayName("TestOps test using REST and UI")
public class EditTestCaseTests extends TestBase {
    Faker faker = new Faker();

    String testCaseNameInitial = faker.artist().name();
    String testCaseNameEdited = faker.book().author();
    String testCaseDescriptionInitial = faker.address().fullAddress();
    String testCaseDescriptionEdited = faker.color().name();
    String firstStep = faker.book().title();
    String secondStep = faker.book().genre();

    @Test
    @Story("Edit test case")
    @Owner("egosteva")
    @DisplayName("Edit name and description of test case")
    @Tag("rest_ui")
    void editTestCaseNameAndDescriptionTest() {
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

        AddDescriptionResponseModel addDescriptionCaseResponse = step("Add description to testcase", () ->
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

        step("Check test case description", () ->
                $("[data-testid=section__description]").shouldHave(text(testCaseDescriptionInitial)));

        step("Change test case name", () -> {
            $(".Menu__trigger").click();
            $(byText("Rename test case")).click();
            $("[placeholder='Enter name']").clear();
            $("[placeholder='Enter name']").setValue(testCaseNameEdited);
            $(byText("Submit")).click();
        });

        step("Check new test case name", () ->
                $(".TestCaseLayout__name").shouldHave(text(testCaseNameEdited)));

        step("Change test case description", () -> {
            $("[data-testid=section__description]").$("[data-testid=button__edit_section]").click();
            $("[data-testid=section__description]").$(".MarkdownTextarea__edit").clear();
            $("[data-testid=section__description]").$(".MarkdownTextarea__edit").setValue(testCaseDescriptionEdited);
            $(byText("Submit")).click();
        });

        step("Check new test case description", () ->
                $("[data-testid=section__description]").shouldHave(text(testCaseDescriptionEdited)));
    }

    @Test
    @Story("Edit test case")
    @Owner("egosteva")
    @DisplayName("Add steps to test case")
    @Tag("rest_ui")
    void addStepsToTestCaseTest() {
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

        step("Add two steps to test case", () -> {
            $("[data-testid=section__scenario]").$("[data-testid=button__edit_section]").click();
            $(".TestCaseScenarioStepEdit__textarea").setValue(firstStep).pressEnter().setValue(secondStep);
            $(byText("Submit")).click();
        });

        step("Check steps name", () -> {
            $(".TreeElement").shouldHave(text(firstStep));
            $(".TreeElement").shouldHave(text(secondStep));
        });
    }
}