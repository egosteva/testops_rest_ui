package com.github.egosteva.tests;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    @BeforeAll
    static void setUp() {
        Configuration.baseUrl = "https://allure.autotests.cloud";
        //       Configuration.holdBrowserOpen = true;
        Configuration.pageLoadStrategy = "eager";
        RestAssured.baseURI = "https://allure.autotests.cloud";
    }
}
