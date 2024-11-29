package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SurveySendDTO {

    private Long surveyId;
    private String voivodeship;
    private String district;
    private String commune;
    private Boolean isOneTime;
    private LocalDate startDate;
    private LocalDate endDate;
}
