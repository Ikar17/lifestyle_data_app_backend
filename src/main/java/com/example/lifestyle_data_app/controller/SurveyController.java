package com.example.lifestyle_data_app.controller;

import com.example.lifestyle_data_app.dto.SurveyDTO;
import com.example.lifestyle_data_app.dto.SurveyMetaDataDTO;
import com.example.lifestyle_data_app.dto.SurveyResponseDTO;
import com.example.lifestyle_data_app.dto.SurveySendDTO;
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

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDTO> getSurveyById(@PathVariable("id") Long id){
        try{
            return new ResponseEntity<>(surveyService.getSurveyById(id), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SurveyDTO> updateSurveyById(@PathVariable("id") Long id,
                                                      @RequestBody SurveyDTO surveyDTO){
        try{
            boolean response = surveyService.updateSurvey(id, surveyDTO);
            if(response){
                return new ResponseEntity<>(HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sending")
    public ResponseEntity<String> sendSurvey(@RequestBody SurveySendDTO surveySendDTO){
        try{
            surveyService.sendingSurvey(surveySendDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/response")
    public ResponseEntity<String> saveSurveyResponse(@RequestBody SurveyResponseDTO response){
        try{
            surveyService.saveResponse(response);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
