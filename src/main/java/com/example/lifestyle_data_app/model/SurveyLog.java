package com.example.lifestyle_data_app.model;

import com.example.lifestyle_data_app.utils.SurveyStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SurveyLog {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JsonIgnore
    private Survey survey;
    @ManyToOne
    @JsonIgnore
    private User user;
    private LocalDate sendAt;
    @Enumerated(EnumType.STRING)
    private SurveyStatus status;
}
