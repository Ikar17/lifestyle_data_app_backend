package com.example.lifestyle_data_app.controller;

import com.example.lifestyle_data_app.model.Comunne;
import com.example.lifestyle_data_app.model.District;
import com.example.lifestyle_data_app.model.Voivodeship;
import com.example.lifestyle_data_app.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@CrossOrigin("*")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @GetMapping("/voivodeships")
    public ResponseEntity<List<Voivodeship>> getVoivodeships(){
        try{
            List<Voivodeship> voivodeships = addressService.getVoivodeships();
            return new ResponseEntity<>(voivodeships, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/districts/{voivodeship}")
    public ResponseEntity<List<District>> getDistricts(@PathVariable String voivodeship){
        try{
            List<District> districts = addressService.getDistrictsByVoivodeship(voivodeship);
            return new ResponseEntity<>(districts, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/communes/{district}")
    public ResponseEntity<List<Comunne>> getCommunes(@PathVariable String district){
        try{
            List<Comunne> comunnes = addressService.getComunnesByDistrict(district);
            return new ResponseEntity<>(comunnes, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
