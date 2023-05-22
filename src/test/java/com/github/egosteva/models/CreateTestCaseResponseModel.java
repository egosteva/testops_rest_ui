package com.github.egosteva.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTestCaseResponseModel {
    Integer id;
    String name;
    Boolean automated, external;
    Long createdDate;
}