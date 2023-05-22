package com.github.egosteva.utils;

import com.github.javafaker.Faker;

public class RandomTestDataUtil {
    Faker faker = new Faker();

    public String getTestCaseName() {
        return faker.artist().name();
    }

  //  public String getTestCaseNameUpdated() {
  //      return faker.book().author();
  //  }

    public String getTestCaseDescription() {
        return faker.address().fullAddress();
    }

 //   public String getTestCaseDescriptionUpdated() {
  //      return faker.color().name();
 //   }

    public String getStepName() {
        return faker.book().title();
    }

    //  public String getStepNameUpdated() {
    //      return faker.book().genre();
    // }

 //   String testCaseNameInitial = faker.artist().name();
 //   String testCaseNameUpdated = faker.book().author();
 //   String testCaseDescriptionInitial = faker.address().fullAddress();
 //   String testCaseDescriptionUpdated = faker.color().name();
 //   String stepNameInitial = faker.book().title();
 //   String stepNameUpdated = faker.book().genre();
}
