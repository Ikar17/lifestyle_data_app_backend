package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SignUpDTO {
    private String uid;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private String voivodeship;
    private String district;
    private String comunne;
}
