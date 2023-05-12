package com.github.egosteva.tests;

import com.github.egosteva.models.AddDescriptionBody;
import com.github.egosteva.models.AddDescriptionResponse;
import com.github.egosteva.models.CreateTestCaseBody;
import com.github.egosteva.models.CreateTestCaseResponse;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;


public class EditTestCaseTests extends TestBase {

    Faker faker = new Faker();
    String login = "allure8",
            password = "allure8",
            projectId = "2257",
            xsrfToken = "50011563-6a05-4b2e-95ca-344ef2831d85",
            allureTestopsSession = "ff19a853-df73-433b-bd22-b4b6c7475f2e";
    String testCaseNameInitial = faker.artist().name();
    String testCaseDescriptionInitial = faker.address().fullAddress();
    String testCaseNameEdited = faker.book().author();
    String testCaseDescriptionEdited = faker.color().name();
    String firstStep = "This is the first step",
            secondStep = "This is the second step";


    @Test
    void editTestCaseNameAndDescriptionTest() {
        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseNameInitial);

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
                        .extract().as(CreateTestCaseResponse.class));
//        assertThat(createTestCaseResponse.getName()).isEqualTo(testCaseNameInitial);

        Integer testCaseId = createTestCaseResponse.getId();

        AddDescriptionBody addDescriptionBody = new AddDescriptionBody();
        addDescriptionBody.setId(testCaseId);
        addDescriptionBody.setDescription(testCaseDescriptionInitial);
        addDescriptionBody.setDescriptionHtml(null);

        AddDescriptionResponse addDescriptionCaseResponse = step("Add description to testcase", () ->
                given()
                        .log().all()
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .cookies("XSRF-TOKEN", xsrfToken,
                                "ALLURE_TESTOPS_SESSION", allureTestopsSession)
                        .contentType("application/json;charset=UTF-8")
                        .body(addDescriptionBody)
                        //                      .queryParam("projectId", projectId)
                        .when()
                        .patch("/api/rs/testcase/" + testCaseId)
                        .then()
                        .log().status()
                        .log().body()
                        .statusCode(200)
                        .extract().as(AddDescriptionResponse.class));

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



}
