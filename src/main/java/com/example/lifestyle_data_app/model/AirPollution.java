package com.example.lifestyle_data_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirPollution {
    @Id
    @GeneratedValue
    private Long id;
    private Double airIndex;
    private Double co;
    private Double no;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double pm2_5;
    private Double pm10;
    private Double nh3;
    private LocalDateTime createdAt;
    @ManyToOne
    private Comunne comunne;
    @PrePersist
    protected void onCreate() {
        if(this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

}
