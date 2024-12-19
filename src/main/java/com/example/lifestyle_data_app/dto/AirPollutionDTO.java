package com.example.lifestyle_data_app.dto;

import lombok.Data;

import java.util.List;

@Data
public class AirPollutionDTO {
    private Coord coord;
    private List<ListItem> list;

    @Data
    public static class Coord {
        private double lon;
        private double lat;
    }

    @Data
    public static class ListItem {
        private long dt;
        private Main main;
        private Components components;
    }

    @Data
    public static class Main {
        private double aqi;
    }

    @Data
    public static class Components {
        private double co; // Tlenek węgla
        private double no; // Tlenek azotu
        private double no2; // Dwutlenek azotu
        private double o3; // Ozon
        private double so2; // Dwutlenek siarki
        private double pm2_5; // Pył zawieszony PM2.5
        private double pm10; // Pył zawieszony PM10
        private double nh3; // Amoniak
    }
}
