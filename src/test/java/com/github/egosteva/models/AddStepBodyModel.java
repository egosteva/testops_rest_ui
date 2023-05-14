package com.github.egosteva.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddStepBodyModel {
    public List<ListStepsData> steps;
    Integer workPath;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListStepsData {
        String name, spacing;
    }
}