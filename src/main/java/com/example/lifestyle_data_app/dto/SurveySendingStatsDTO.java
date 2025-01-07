package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SurveySendingStatsDTO {
    private Long surveyId;
    private LocalDate sendAt;
    private Long count;

    public SurveySendingStatsDTO(Long surveyId, LocalDate sendAt, Long count) {
        this.surveyId = surveyId;
        this.sendAt = sendAt;
        this.count = count;
    }
}
