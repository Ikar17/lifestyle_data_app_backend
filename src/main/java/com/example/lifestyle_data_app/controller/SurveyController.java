package com.example.lifestyle_data_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/survey")
@CrossOrigin("*")
public class SurveyController {

    @PostMapping("/create")
    public ResponseEntity<String> createSurvey(){
        //only for testing firebase configuration
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
