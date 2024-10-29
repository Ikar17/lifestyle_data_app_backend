package com.example.lifestyle_data_app.model;

import com.example.lifestyle_data_app.utils.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private Long uid;
    private String name;
    private String surname;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne
    private Address address;

}
