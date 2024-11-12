package com.example.lifestyle_data_app.model;

import com.example.lifestyle_data_app.utils.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String uid;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne
    private Address address;

}
