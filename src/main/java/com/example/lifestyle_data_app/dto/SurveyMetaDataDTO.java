package com.example.lifestyle_data_app.dto;

import com.example.lifestyle_data_app.model.Survey;
import com.example.lifestyle_data_app.model.SurveyLog;
import lombok.Data;

import java.util.List;

@Data
public class SurveyMetaDataDTO {
    private Survey survey;
    private AuthorDTO author;
    private List<SurveyLog> surveyLog;
    private Boolean editable;
}
