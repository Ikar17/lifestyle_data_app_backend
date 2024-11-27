package com.example.lifestyle_data_app.dto;

import com.example.lifestyle_data_app.model.Survey;
import com.example.lifestyle_data_app.model.SurveyLog;
import lombok.Data;

@Data
public class SurveyMetaDataDTO {
    private Survey survey;
    private AuthorDTO author;
    private SurveyLog surveyLog;
    private Boolean editable;
}
