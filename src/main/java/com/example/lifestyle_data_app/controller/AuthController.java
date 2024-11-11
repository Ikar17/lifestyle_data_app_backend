package com.example.lifestyle_data_app.controller;

import com.example.lifestyle_data_app.dto.SignUpDTO;
import com.example.lifestyle_data_app.model.User;
import com.example.lifestyle_data_app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpDTO signUpDTO){
        try{
            User user = authService.addUser(signUpDTO);
            if(user == null) new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
