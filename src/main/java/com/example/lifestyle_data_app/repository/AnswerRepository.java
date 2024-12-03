package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySurveyResponse_Id(Long id);
}
