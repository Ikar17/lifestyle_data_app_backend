package com.example.lifestyle_data_app.model;


import com.example.lifestyle_data_app.utils.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Survey survey;
    private String description;
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AnswerOption> answerOptions;

}
