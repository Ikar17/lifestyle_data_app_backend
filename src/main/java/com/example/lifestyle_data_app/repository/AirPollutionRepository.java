package com.example.lifestyle_data_app.repository;

import com.example.lifestyle_data_app.dto.HourlyAverageAirPollutionDTO;
import com.example.lifestyle_data_app.model.AirPollution;
import com.example.lifestyle_data_app.model.Comunne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AirPollutionRepository extends JpaRepository<AirPollution, Long> {
    @Query("SELECT MAX(a.createdAt) FROM AirPollution a")
    LocalDateTime findLastFetchTime();

    AirPollution findTopByComunneOrderByCreatedAtDesc(Comunne comunne);

    @Query("SELECT new com.example.lifestyle_data_app.dto.HourlyAverageAirPollutionDTO( " +
            "ap.createdAt, " +
            "AVG(ap.airIndex), " +
            "AVG(ap.co), " +
            "AVG(ap.no), " +
            "AVG(ap.no2), " +
            "AVG(ap.o3), " +
            "AVG(ap.so2), " +
            "AVG(ap.pm2_5), " +
            "AVG(ap.pm10), " +
            "AVG(ap.nh3)) " +
            "FROM AirPollution ap " +
            "JOIN ap.comunne c " +
            "JOIN c.district d " +
            "JOIN d.voivodeship v " +
            "WHERE v.name = :voivodeship " +
            "AND ap.createdAt BETWEEN :fromDate AND :toDate " +
            "GROUP BY ap.createdAt " +
            "ORDER BY ap.createdAt ASC")
    List<HourlyAverageAirPollutionDTO> findAverageAirPollutionByVoivodeshipAndDateRange(
            @Param("voivodeship") String voivodeship,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);


    @Query("SELECT new com.example.lifestyle_data_app.dto.HourlyAverageAirPollutionDTO( " +
            "ap.createdAt, " +
            "AVG(ap.airIndex), " +
            "AVG(ap.co), " +
            "AVG(ap.no), " +
            "AVG(ap.no2), " +
            "AVG(ap.o3), " +
            "AVG(ap.so2), " +
            "AVG(ap.pm2_5), " +
            "AVG(ap.pm10), " +
            "AVG(ap.nh3)) " +
            "FROM AirPollution ap " +
            "JOIN ap.comunne c " +
            "JOIN c.district d " +
            "WHERE d.name = :district " +
            "AND ap.createdAt BETWEEN :fromDate AND :toDate " +
            "GROUP BY ap.createdAt " +
            "ORDER BY ap.createdAt ASC")
    List<HourlyAverageAirPollutionDTO> findAverageAirPollutionByDistrictAndDateRange(
            @Param("district") String district,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query("SELECT new com.example.lifestyle_data_app.dto.HourlyAverageAirPollutionDTO( " +
            "ap.createdAt, " +
            "AVG(ap.airIndex), " +
            "AVG(ap.co), " +
            "AVG(ap.no), " +
            "AVG(ap.no2), " +
            "AVG(ap.o3), " +
            "AVG(ap.so2), " +
            "AVG(ap.pm2_5), " +
            "AVG(ap.pm10), " +
            "AVG(ap.nh3)) " +
            "FROM AirPollution ap " +
            "JOIN ap.comunne c " +
            "WHERE c.name = :comunne " +
            "AND ap.createdAt BETWEEN :fromDate AND :toDate " +
            "GROUP BY ap.createdAt " +
            "ORDER BY ap.createdAt ASC")
    List<HourlyAverageAirPollutionDTO> findAverageAirPollutionByComunneAndDateRange(
            @Param("comunne") String comunne,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
}
