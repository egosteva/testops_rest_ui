package com.github.egosteva.tests;

import com.github.egosteva.models.*;
import com.github.egosteva.pages.TestCasesPage;
import com.github.egosteva.utils.RandomTestDataUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.egosteva.specifications.Specifications.requestSpec;
import static com.github.egosteva.specifications.Specifications.responseSpec;
import static com.github.egosteva.tests.TestData.projectId;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

@Story("Update test case")
@Owner("egosteva")
@Feature("Update TestOps test case using REST and UI")
@DisplayName("Update TestOps test case using REST and UI")
@Tag("update_testcase")
public class UpdateTestCaseTests extends TestBase {
    TestCasesPage testCasesPage = new TestCasesPage();
    RandomTestDataUtil randomTestDataUtil = new RandomTestDataUtil();
    String testCaseNameInitial = randomTestDataUtil.getTestCaseName();
    String stepNameInitial = randomTestDataUtil.getStepName();
    Integer testCaseId;

    @BeforeEach
    public void createTestCase() {
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

        testCaseId = createTestCaseResponse.getId();
    }

    @Test
    @DisplayName("Edit name and description of test case")
    void updateTestCaseNameAndDescriptionTest() {
        String testCaseDescriptionInitial = randomTestDataUtil.getTestCaseDescription();
        String testCaseNameUpdated = randomTestDataUtil.getTestCaseName();
        String testCaseDescriptionUpdated = randomTestDataUtil.getTestCaseDescription();

        AddDescriptionBodyModel addDescriptionBody = new AddDescriptionBodyModel();
        addDescriptionBody.setId(testCaseId);
        addDescriptionBody.setDescription(testCaseDescriptionInitial);
        addDescriptionBody.setDescriptionHtml(null);

        CreateTestCaseResponseModel addDescriptionResponse = step("Add description to testcase", () ->
                given(requestSpec)
                        .body(addDescriptionBody)
                        .when()
                        .patch("/testcase/" + testCaseId)
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        step("Open test case editor", () ->
                testCasesPage.openTestCaseEditor(projectId, testCaseId));

        step("Check test case name", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseNameInitial));

        step("Check description", () ->
                testCasesPage.checkTestCaseDescription(testCaseDescriptionInitial));

        step("Update test case name", () ->
                testCasesPage.updateTestCaseName(testCaseNameUpdated));

        step("Check updated test case name", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseNameUpdated));

        step("Update description", () ->
                testCasesPage.updateTestCaseDescription(testCaseDescriptionUpdated));

        step("Check updated description", () ->
                testCasesPage.checkTestCaseDescription(testCaseDescriptionUpdated));
    }

    @Test
    @DisplayName("Add step to test case")
    void addStepToTestCaseTest() {
        step("Open test case editor", () ->
                testCasesPage.openTestCaseEditor(projectId, testCaseId));

        step("Check test case name", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseNameInitial));

        step("Add step to test case", () ->
                testCasesPage.addStepToTestCase(stepNameInitial));

        step("Check step name", () ->
                testCasesPage.checkStepName(stepNameInitial));
    }

    @Test
    @DisplayName("Update step of test case")
    void updateStepOfTestCaseTest() {
        String stepNameUpdated = randomTestDataUtil.getStepName();

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

        step("Open test case editor", () ->
                testCasesPage.openTestCaseEditor(projectId, testCaseId));

        step("Check test case name", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseNameInitial));

        step("Check step name", () ->
                testCasesPage.checkStepName(stepNameInitial));

        step("Edit step of test case", () ->
                testCasesPage.updateStepName(stepNameUpdated));

        step("Check updated step name", () ->
                testCasesPage.checkStepName(stepNameUpdated));
    }
}