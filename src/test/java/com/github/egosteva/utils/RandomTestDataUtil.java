package com.github.egosteva.utils;

import com.github.javafaker.Faker;

public class RandomTestDataUtil {
    Faker faker = new Faker();

    public String getTestCaseName() {
        return faker.artist().name();
    }

    public String getTestCaseDescription() {
        return faker.address().fullAddress();
    }

    public String getStepName() {
        return faker.book().title();
    }
}