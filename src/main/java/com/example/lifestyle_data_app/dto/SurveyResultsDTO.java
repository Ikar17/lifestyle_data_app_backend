package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.util.List;

@Data
public class SurveyResultsDTO {
    private Long surveyId;
    private String title;
    private Long sentCount;
    private Long completeCount;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO{
        private String question;
        private String questionType;
        List<AnswerResultDTO> results;
    }
    @Data
    public static class AnswerResultDTO {
        private String answer;
        private Long count;
    }
}


