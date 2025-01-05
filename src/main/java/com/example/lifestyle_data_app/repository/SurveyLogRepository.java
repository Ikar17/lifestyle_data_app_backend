package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.SurveyLog;
import com.example.lifestyle_data_app.model.User;
import com.example.lifestyle_data_app.utils.SurveyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SurveyLogRepository extends JpaRepository<SurveyLog, Long> {
    Page<SurveyLog> findAllByUserAndSendAtBefore(User user, LocalDate date, Pageable pageable);
    List<SurveyLog> findAllBySurvey_Id(Long surveyId);
    void removeAllBySurvey_Id(Long surveyId);
    long countAllBySurvey_IdAndStatus(Long surveyId, SurveyStatus status);
    void removeAllByUser(User user);
}
