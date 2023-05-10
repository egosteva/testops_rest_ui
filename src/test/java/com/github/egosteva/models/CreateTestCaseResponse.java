package com.github.egosteva.models;

import lombok.Data;

@Data
public class CreateTestCaseResponse {

    Integer id;
    String name, statusName, statusColor;
    Boolean automated, external;
    Long createdDate;
}
