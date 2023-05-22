package com.github.egosteva.tests;

import com.github.egosteva.models.CreateTestCaseBodyModel;
import com.github.egosteva.models.CreateTestCaseResponseModel;
import com.github.egosteva.pages.LoginPage;
import com.github.egosteva.pages.TestCasesPage;
import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.egosteva.specifications.Specifications.requestSpec;
import static com.github.egosteva.specifications.Specifications.responseSpec;
import static com.github.egosteva.tests.TestData.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

@Story("Create test case using only UI")
@Owner("egosteva")
@Feature("Create TestOps test case")
@DisplayName("Create TestOps test case")
@Tag("create_testcase")
public class CreateTestCaseTests extends TestBase {
    LoginPage loginPage = new LoginPage();
    TestCasesPage testCasesPage = new TestCasesPage();
    Faker faker = new Faker();

    @Test
    @DisplayName("Create test case using only UI")
    void createWithUiOnlyTest() {
        String testCaseName = faker.name().fullName();

        step("Authorize", () -> {
            loginPage.openPage()
                    .setLogin(login)
                    .setPassword(password)
                    .clickSubmit();
            testCasesPage.checkSideMenu();
        });

        step("Go to project", () ->
                testCasesPage.openProjectPage());

        step("Create test case", () ->
                testCasesPage.setTestcaseName(testCaseName));

        step("Check test case name", () ->
                testCasesPage.checkTestCaseNameAtSideBar(testCaseName));
    }

    @Test
    @DisplayName("Create test case using API and UI")
    void createWithApiAndUiTest() {
        String testCaseName = faker.name().fullName();

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

        step("Open test case editor", () ->
                testCasesPage.openTestCaseEditor(projectId, testCaseId));

        step("Check test case name", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseName));
    }
}