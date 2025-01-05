package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Survey;
import com.example.lifestyle_data_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Page<Survey> getAllByAuthor(User author, Pageable pageable);
    List<Survey> getAllByAuthor(User author);
}
