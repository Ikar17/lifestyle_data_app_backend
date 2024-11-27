package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.util.List;

@Data
public class SurveyItemDTO {
    private long id;
    private String type;
    private String text;
    private List<String> options;
}
