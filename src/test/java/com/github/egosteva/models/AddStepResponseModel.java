package com.github.egosteva.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Nullable
public class AddStepResponseModel {
    public List<ListStepsResponseData> steps;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListStepsResponseData {
    public String name;
    Integer stepsCount;
    boolean hasContent, leaf;
    }
}