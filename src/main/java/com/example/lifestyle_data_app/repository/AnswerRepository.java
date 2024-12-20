package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Answer;
import com.example.lifestyle_data_app.model.AnswerOption;
import com.example.lifestyle_data_app.model.Question;
import com.example.lifestyle_data_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySurveyResponse_Id(Long id);
    void removeAllBySurveyResponse_SurveyLog_Survey_Id(Long surveyId);
    long countAllByQuestionAndAnswerOption(Question question, AnswerOption option);
    List<Answer> findAllByQuestion(Question question);
    void removeAllBySurveyResponse_User(User user);
}
