package com.example.lifestyle_data_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private SurveyResponse surveyResponse;
    @ManyToOne
    private Question question;
    @ManyToMany(cascade = CascadeType.REMOVE)
    private List<AnswerOption> answerOption;
    private String answer;
}
