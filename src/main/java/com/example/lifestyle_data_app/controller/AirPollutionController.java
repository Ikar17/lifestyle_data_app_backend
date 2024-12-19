package com.example.lifestyle_data_app.controller;

import com.example.lifestyle_data_app.dto.HourlyAverageAirPollutionDTO;
import com.example.lifestyle_data_app.model.AirPollution;
import com.example.lifestyle_data_app.service.AirPollutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/air")
public class AirPollutionController {

    @Autowired
    private AirPollutionService airPollutionService;
    @GetMapping
    public ResponseEntity<List<HourlyAverageAirPollutionDTO>> getAirQualityData(
            @RequestParam(defaultValue = "") String voivodeship,
            @RequestParam(defaultValue = "") String district,
            @RequestParam(defaultValue = "") String commune,
            @RequestParam(defaultValue = "") String dateFrom,
            @RequestParam(defaultValue = "") String dateTo) {
        try{
            List<HourlyAverageAirPollutionDTO> results = airPollutionService.getAirQualityData(voivodeship, district, commune, dateFrom, dateTo);
            return new ResponseEntity<>(results, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<AirPollution> getLastUserAirQualityData() {
        try{
            AirPollution result = airPollutionService.getLastUserAirQualityData();
            if(result == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
