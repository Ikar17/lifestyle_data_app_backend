package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.dto.AirPollutionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AirPollutionApiClient {
    private final RestTemplate restTemplate;
    @Value("${application.api-key}")
    private String api_key;

    public AirPollutionApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }
    public AirPollutionDTO fetchHistoricalAirQuality(Float lat, Float lon, LocalDateTime startTime, LocalDateTime endTime) {
        long unixStartTime = startTime.toEpochSecond(ZoneOffset.UTC);
        long unixEndTime = endTime.toEpochSecond(ZoneOffset.UTC);

        String url = "http://api.openweathermap.org/data/2.5/air_pollution/history?lat=" +
                lat.toString()+
                "&lon="+lon.toString()+
                "&start="+unixStartTime+
                "&end="+unixEndTime+
                "&appid="+api_key;

        return restTemplate.getForObject(url, AirPollutionDTO.class);
    }
}
