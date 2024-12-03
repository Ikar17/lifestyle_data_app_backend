package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.SurveyLog;
import com.example.lifestyle_data_app.model.User;
import com.example.lifestyle_data_app.utils.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyLogRepository extends JpaRepository<SurveyLog, Long> {
    List<SurveyLog> findAllByUser(User user);
    List<SurveyLog> findAllBySurvey_Id(Long surveyId);
    void removeAllBySurvey_Id(Long surveyId);
    long countAllBySurvey_IdAndStatus(Long surveyId, SurveyStatus status);
}
