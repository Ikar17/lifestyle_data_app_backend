package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findAllByVoivodeshipName(String voivodeshipName);
    District findByName(String name);
}
