package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HourlyAverageAirPollutionDTO {
    private LocalDateTime createdAt;
    private Double airIndex;
    private Double co;
    private Double no;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double pm2_5;
    private Double pm10;
    private Double nh3;

    public HourlyAverageAirPollutionDTO(LocalDateTime createdAt, Double airIndex, Double co, Double no, Double no2, Double o3, Double so2, Double pm2_5, Double pm10, Double nh3) {
        this.createdAt = createdAt;
        this.airIndex = airIndex;
        this.co = co;
        this.no = no;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.nh3 = nh3;
    }
}
