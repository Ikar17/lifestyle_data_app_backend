package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.dto.SignUpDTO;
import com.example.lifestyle_data_app.model.Address;
import com.example.lifestyle_data_app.model.User;
import com.example.lifestyle_data_app.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AddressService addressService;
    public User addUser(SignUpDTO signUpDTO){
        try{
            Address address = addressService.addAddress(signUpDTO.getVoivodeship(), signUpDTO.getDistrict(), signUpDTO.getComunne());
            if(address == null) return null;

            User user = new User();
            user.setAddress(address);
            user.setRole(Role.USER);
            user.setName(signUpDTO.getName());
            user.setSurname(signUpDTO.getSurname());
            user.setEmail(signUpDTO.getEmail());
            user.setUid(signUpDTO.getUid());
            user.setBirthDate(signUpDTO.getBirthDate());

            return user;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }
}
