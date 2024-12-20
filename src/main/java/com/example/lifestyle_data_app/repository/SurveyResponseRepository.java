package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.SurveyResponse;
import com.example.lifestyle_data_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    Optional<SurveyResponse> findBySurveyLog_Id(Long id);
    void removeAllBySurveyLog_Survey_Id(Long id);
    void removeAllByUser(User user);
}
