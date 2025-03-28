package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.dto.SignUpDTO;
import com.example.lifestyle_data_app.model.Address;
import com.example.lifestyle_data_app.model.User;
import com.example.lifestyle_data_app.repository.UserRepository;
import com.example.lifestyle_data_app.utils.Role;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SurveyService surveyService;

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

            return userRepository.save(user);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }

    public String getRole(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> userOptional = userRepository.findByUid(authentication.getName());
            return userOptional.map(user -> user.getRole().toString()).orElse(null);
        }catch(Exception e){
            return null;
        }
    }
    public User getUser() throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userRepository.findByUid(authentication.getName());
        if (userOptional.isEmpty()) throw new Exception("User not found");
        return userOptional.get();
    }

    public boolean updateUserData(SignUpDTO userData){
        try{
            User user = getUser();
            if(userData.getBirthDate() != null) user.setBirthDate(userData.getBirthDate());
            if(userData.getName() != null) user.setName(userData.getName());
            if(userData.getSurname() != null) user.setSurname(userData.getSurname());

            if(userData.getVoivodeship() != null && userData.getComunne() != null && userData.getDistrict() != null
                    && (!userData.getVoivodeship().equals(user.getAddress().getVoivodeship().getName())
                        || !userData.getDistrict().equals(user.getAddress().getDistrict().getName())
                        || !userData.getComunne().equals(user.getAddress().getComunne().getName()))
            ){
                Address address = addressService.addAddress(userData.getVoivodeship(), userData.getDistrict(), userData.getComunne());
                user.setAddress(address);

            }
            userRepository.save(user);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public User findUserByUid(String uid){
        Optional<User> userOptional = userRepository.findByUid(uid);
        return userOptional.orElse(null);
    }

    public void deleteUser(User user){
        surveyService.removeAllByUser(user);
        userRepository.delete(user);
        try{
            FirebaseAuth.getInstance().deleteUser(user.getUid());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void changeUserRole(User user, String role){
        if(role.equals(Role.ADMIN.toString())){
            user.setRole(Role.ADMIN);
        }else if(role.equals(Role.ANALYST.toString())){
            user.setRole(Role.ANALYST);
        }else if(role.equals(Role.USER.toString())){
            user.setRole(Role.USER);
        }else return;

        userRepository.save(user);
    }
    public Page<User> getUsersExcludingCurrent(int page, int size, String sortEmail, String sortRole) {
        try{
            User user = getUser();
            Sort sort = buildSort(sortEmail, sortRole);
            Pageable pageable = PageRequest.of(page, size, sort);
            return userRepository.findByIdNot(user.getId(), pageable);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }
    private Sort buildSort(String sortEmail, String sortRole) {
        Sort sort = Sort.unsorted();
        if (!sortRole.equals("-")) {
            sort = sort.and(Sort.by(sortRole.equalsIgnoreCase("asc") ? Sort.Order.asc("role") : Sort.Order.desc("role")));
        }
        if (!sortEmail.equals("-")) {
            sort = sort.and(Sort.by(sortEmail.equalsIgnoreCase("asc") ? Sort.Order.asc("email") : Sort.Order.desc("email")));
        }
        return sort;
    }

}
