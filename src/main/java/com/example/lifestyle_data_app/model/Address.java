package com.example.lifestyle_data_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Voivodeship voivodeship;
    @ManyToOne
    private District district;
    @ManyToOne
    private Comunne comunne;

}
