package com.example.lifestyle_data_app.model;

import com.example.lifestyle_data_app.utils.SurveyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SurveyLog {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Survey survey;
    @ManyToOne
    private User user;
    private LocalDateTime sendAt;
    @Enumerated(EnumType.STRING)
    private SurveyStatus status;
}
