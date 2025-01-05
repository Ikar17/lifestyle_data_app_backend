package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(String uid);
    @Query("SELECT u FROM User u " +
            "WHERE (:voivodeship IS NULL OR :voivodeship = '' OR u.address.voivodeship.name = :voivodeship) " +
            "AND (:district IS NULL OR :district = '' OR u.address.district.name = :district) " +
            "AND (:commune IS NULL OR :commune = '' OR u.address.comunne.name = :commune)"+
            "AND u.role = 'USER'")
    List<User> findUsersByAddress(
            @Param("voivodeship") String voivodeship,
            @Param("district") String district,
            @Param("commune") String commune);
    Page<User> findByIdNot(Long id, Pageable pageable);
}
