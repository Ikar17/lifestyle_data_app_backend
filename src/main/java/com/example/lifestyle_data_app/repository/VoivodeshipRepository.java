package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Voivodeship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoivodeshipRepository extends JpaRepository<Voivodeship, Integer> {
    Voivodeship findByName(String name);
}
