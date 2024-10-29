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
public class MeasuringStation {
    @Id
    private Integer id;
    @ManyToOne
    private Voivodeship voivodeship;
    @ManyToOne
    private District district;
    @ManyToOne
    private Comunne comunne;
    private String street;
}
