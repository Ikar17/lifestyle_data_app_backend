package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Answer;
import com.example.lifestyle_data_app.model.AnswerOption;
import com.example.lifestyle_data_app.model.Question;
import com.example.lifestyle_data_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySurveyResponse_Id(Long id);
    void removeAllBySurveyResponse_SurveyLog_Survey_Id(Long surveyId);
    long countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetween(Question question, AnswerOption option, LocalDateTime startDate, LocalDateTime endDate);
    long countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Voivodeship_Name(Question question, AnswerOption option, LocalDateTime startDate, LocalDateTime endDate, String voivodeship);
    long countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_District_Name(Question question, AnswerOption option, LocalDateTime startDate, LocalDateTime endDate, String district);
    long countAllByQuestionAndAnswerOptionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Comunne_Name(Question question, AnswerOption option, LocalDateTime startDate, LocalDateTime endDate, String comunne);

    List<Answer> findAllByQuestionAndSurveyResponse_CreatedAtBetween(Question question, LocalDateTime startDate, LocalDateTime endDate);
    List<Answer> findAllByQuestionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Voivodeship_Name(Question question, LocalDateTime startDate, LocalDateTime endDate, String voivodeship);

    List<Answer> findAllByQuestionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_District_Name(Question question, LocalDateTime startDate, LocalDateTime endDate, String district);

    List<Answer> findAllByQuestionAndSurveyResponse_CreatedAtBetweenAndSurveyResponse_User_Address_Comunne_Name(Question question, LocalDateTime startDate, LocalDateTime endDate, String comunne);

    void removeAllBySurveyResponse_User(User user);
    void removeAllBySurveyResponse_CreatedAtBetweenAndSurveyResponse_SurveyLog_Survey_Id(LocalDateTime dateStart, LocalDateTime dateEnd, Long surveyId);
}
