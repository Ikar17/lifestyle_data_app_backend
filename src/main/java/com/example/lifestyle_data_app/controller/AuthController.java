package com.example.lifestyle_data_app.controller;

import com.example.lifestyle_data_app.dto.SignUpDTO;
import com.example.lifestyle_data_app.model.User;
import com.example.lifestyle_data_app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
            if(user == null) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            System.out.println("New user: " + user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole(){
        try{
            return new ResponseEntity<>(authService.getRole(), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/details")
    public ResponseEntity<User> getUserData(){
        try{
            return new ResponseEntity<>(authService.getUser(), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUserData(@RequestBody SignUpDTO userDetails){
        try{
            if(authService.updateUserData(userDetails)){
                return new ResponseEntity<>(authService.getUser(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(authService.getUser(), HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMyAccount(){
        try{
            User user = authService.getUser();
            authService.deleteUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUserAccount(@Param("uid") String uid){
        try{
            User user = authService.findUserByUid(uid);
            if(user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            authService.deleteUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/role")
    public ResponseEntity<String> changeUserRole(@Param("uid") String uid, @Param("role") String role){
        try{
            User user = authService.findUserByUid(uid);
            if(user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            authService.changeUserRole(user, role);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size,
                                               @RequestParam(defaultValue = "-") String sortEmail,
                                               @RequestParam(defaultValue = "-") String sortRole){
        try{
            return new ResponseEntity<>(authService.getUsersExcludingCurrent(page, size, sortEmail, sortRole), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
