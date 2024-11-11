package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Comunne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComunneRepository extends JpaRepository<Comunne, Integer> {
    List<Comunne> findAllByDistrict_Name(String districtName);
    Comunne findByName(String name);
}
