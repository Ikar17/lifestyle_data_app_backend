package com.example.lifestyle_data_app.service;

import com.example.lifestyle_data_app.dto.AirPollutionDTO;
import com.example.lifestyle_data_app.dto.HourlyAverageAirPollutionDTO;
import com.example.lifestyle_data_app.model.AirPollution;
import com.example.lifestyle_data_app.model.Comunne;
import com.example.lifestyle_data_app.model.User;
import com.example.lifestyle_data_app.repository.AirPollutionRepository;
import com.example.lifestyle_data_app.repository.ComunneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AirPollutionService {
    @Autowired
    private AirPollutionRepository airPollutionRepository;
    @Autowired
    private ComunneRepository comunneRepository;
    @Autowired
    private AirPollutionApiClient airPollutionApiClient;
    @Autowired
    private AuthService authService;

    public List<HourlyAverageAirPollutionDTO> getAirQualityData(String voivodeship, String district, String commune, String dateFromString, String dateToString){
        try{
            User user = authService.getUser();
            if(commune.equals("") && district.equals("") && voivodeship.equals("")){
                commune = user.getAddress().getComunne().getName();
            }

            List<HourlyAverageAirPollutionDTO> results;
            LocalDateTime dateFrom;
            LocalDateTime dateTo;

            if(dateFromString.equals("") && dateToString.equals("")){
                dateTo = LocalDateTime.now();
                dateFrom = dateTo.minusDays(3);
            }else if(dateFromString.equals("")){
                dateTo = LocalDateTime.parse(dateToString);
                dateFrom = dateTo.minusDays(3);
            }else if(dateToString.equals("")){
                dateFrom = LocalDateTime.parse(dateFromString);
                dateTo = LocalDateTime.now();
            }else{
                dateFrom = LocalDateTime.parse(dateFromString);
                dateTo = LocalDateTime.parse(dateToString);
            }

            if(commune.equals("")){
                results = calculateDateByDistrictOrVoivodeship(voivodeship, district, dateFrom, dateTo);
            }else{
                results = airPollutionRepository.findAverageAirPollutionByComunneAndDateRange(commune, dateFrom, dateTo);
            }

            return results;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public AirPollution getLastUserAirQualityData(){
        try{
            User user = authService.getUser();
            Comunne comunne = user.getAddress().getComunne();
            return airPollutionRepository.findTopByComunneOrderByCreatedAtDesc(comunne);
        }catch(Exception e){
            return null;
        }
    }

    public void getAirQualityDataCSVFile(String voivodeship, String district, String commune, String dateFromString, String dateToString, PrintWriter writer){
        List<HourlyAverageAirPollutionDTO> data = getAirQualityData(voivodeship, district, commune, dateFromString, dateToString);
        if(data == null) return;

        String header = "Czas;indeks;co;no;no2;o3;so2;pm2_5;pm10;nh3";
        writer.println(header);

        StringBuilder builder = new StringBuilder();
        for(HourlyAverageAirPollutionDTO item : data){
            builder.append(item.getCreatedAt().toString());
            builder.append(";");
            builder.append(item.getAirIndex().toString());
            builder.append(";");
            builder.append(item.getCo().toString());
            builder.append(";");
            builder.append(item.getNo().toString());
            builder.append(";");
            builder.append(item.getNo2().toString());
            builder.append(";");
            builder.append(item.getO3().toString());
            builder.append(";");
            builder.append(item.getSo2().toString());
            builder.append(";");
            builder.append(item.getPm2_5().toString());
            builder.append(";");
            builder.append(item.getPm10().toString());
            builder.append(";");
            builder.append(item.getNh3().toString());

            writer.println(builder);
            builder.setLength(0);
        }
    }


    @Scheduled(fixedRate = 10800000) //3h + ~1h to fetch data
    public void fillHistoricalData() {
        LocalDateTime lastFetchTime = airPollutionRepository.findLastFetchTime();
        LocalDateTime now = LocalDateTime.now();
        if (lastFetchTime == null) {
            lastFetchTime = now.minusHours(1);
        }
        if (lastFetchTime.isAfter(now.minusHours(1))) return;

        System.out.println(now);
        System.out.println(lastFetchTime);

        List<Comunne> comunnes = comunneRepository.findAll();
        int batchSize = 60;

        System.out.println("Fetching hourly air quality data...");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        AtomicInteger currentIndex = new AtomicInteger(0);
        LocalDateTime finalLastFetchTime = lastFetchTime;

        //due to limitation of free subscription
        scheduler.scheduleAtFixedRate(() -> {
            int index = currentIndex.get();
            if (index >= comunnes.size()) {
                scheduler.shutdown();
                System.out.println("Fetching data is done.");
                return;
            }

            List<Comunne> batch = comunnes.subList(
                    index,
                    Math.min(index + batchSize, comunnes.size())
            );

            for (Comunne comunne : batch) {
                try {
                    AirPollutionDTO data = airPollutionApiClient.fetchHistoricalAirQuality(
                            comunne.getLan(),
                            comunne.getLon(),
                            finalLastFetchTime,
                            now
                    );
                    saveAirData(comunne, data);
                } catch (Exception e) {
                    System.out.println("Error fetching data for commune " + comunne.getName() + ": " + e.getMessage());
                }
            }

            currentIndex.addAndGet(batchSize);
        }, 0, 1, TimeUnit.MINUTES);
    }


    private void saveAirData(Comunne comunne, AirPollutionDTO data){
        if(data == null || data.getList() == null || comunne == null) return;
        for(AirPollutionDTO.ListItem item : data.getList()){
            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(item.getDt(), 0, ZoneOffset.UTC);

            AirPollution airPollution = new AirPollution();
            airPollution.setComunne(comunne);
            airPollution.setCreatedAt(localDateTime);
            airPollution.setAirIndex(item.getMain().getAqi());
            airPollution.setNo(item.getComponents().getNo());
            airPollution.setCo(item.getComponents().getCo());
            airPollution.setO3(item.getComponents().getO3());
            airPollution.setNh3(item.getComponents().getNh3());
            airPollution.setNo2(item.getComponents().getNo2());
            airPollution.setPm2_5(item.getComponents().getPm2_5());
            airPollution.setPm10(item.getComponents().getPm10());
            airPollution.setSo2(item.getComponents().getSo2());

            airPollutionRepository.save(airPollution);
        }
    }

    private List<HourlyAverageAirPollutionDTO> calculateDateByDistrictOrVoivodeship(String voivodeship, String district, LocalDateTime dateFrom, LocalDateTime dateTo){
        if(voivodeship.equals("") && district.equals("")) return null;

        List<HourlyAverageAirPollutionDTO> results;
        if(district.equals("")){
            results = airPollutionRepository.findAverageAirPollutionByVoivodeshipAndDateRange(voivodeship, dateFrom, dateTo);
        }else{
            results = airPollutionRepository.findAverageAirPollutionByDistrictAndDateRange(district, dateFrom, dateTo);
            System.out.println(results);
        }
        return results;
    }
}
