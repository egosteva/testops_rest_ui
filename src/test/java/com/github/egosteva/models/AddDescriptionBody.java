package com.github.egosteva.models;

import lombok.Data;

@Data
public class AddDescriptionBody {
    Integer id;
    String description, descriptionHtml;
}
