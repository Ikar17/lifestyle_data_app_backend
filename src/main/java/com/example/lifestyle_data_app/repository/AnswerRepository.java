package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Answer;
import com.example.lifestyle_data_app.model.AnswerOption;
import com.example.lifestyle_data_app.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySurveyResponse_Id(Long id);
    void removeAllBySurveyResponse_SurveyLog_Survey_Id(Long surveyId);
    long countAllByQuestionAndAnswerOption(Question question, AnswerOption option);
    List<Answer> findAllByQuestion(Question question);
}
