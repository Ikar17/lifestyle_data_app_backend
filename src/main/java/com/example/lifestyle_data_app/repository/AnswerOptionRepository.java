package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
}
