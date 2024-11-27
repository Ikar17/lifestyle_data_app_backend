package com.example.lifestyle_data_app.controller;

import com.example.lifestyle_data_app.dto.SurveyDTO;
import com.example.lifestyle_data_app.dto.SurveyMetaDataDTO;
import com.example.lifestyle_data_app.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/survey")
@CrossOrigin("*")
public class SurveyController {
    @Autowired
    private SurveyService surveyService;

    @PostMapping("/create")
    public ResponseEntity<String> createSurvey(@RequestBody SurveyDTO surveyDTO){
        try{
            surveyService.createSurvey(surveyDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<SurveyMetaDataDTO>> getSurveys(){
        try{
            return new ResponseEntity<>(surveyService.getSurveys(), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
