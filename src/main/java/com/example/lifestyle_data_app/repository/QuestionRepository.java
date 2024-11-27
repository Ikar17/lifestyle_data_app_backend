package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Question;
import com.example.lifestyle_data_app.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllBySurvey(Survey survey);
    void removeAllBySurvey_Id(Long surveyId);
}
