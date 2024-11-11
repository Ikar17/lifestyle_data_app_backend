package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SignUpDTO {
    private String uid;
    private String name;
    private String surname;
    private String email;
    private LocalDateTime birthDate;
    private String voivodeship;
    private String district;
    private String comunne;
}
