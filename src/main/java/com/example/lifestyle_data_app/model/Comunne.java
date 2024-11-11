package com.example.lifestyle_data_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comunne {
    @Id
    private Integer id;
    private String name;
    private Float lan;
    private Float lon;
    @ManyToOne
    private District district;
}
