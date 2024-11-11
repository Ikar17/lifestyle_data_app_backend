package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.Address;
import com.example.lifestyle_data_app.model.Voivodeship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
}
