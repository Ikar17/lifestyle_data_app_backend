package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.util.List;

@Data
public class SurveyResponseDTO {
    private Long surveyId;
    private Long surveyLogId;
    private List<SurveyItemDTO> answers;
}
