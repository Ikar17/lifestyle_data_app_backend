package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.util.List;

@Data
public class SurveyDTO {
    private List<SurveyItemDTO> items;
    private String title;
    private String description;
    private SurveyMetaDataDTO metaData;

}
