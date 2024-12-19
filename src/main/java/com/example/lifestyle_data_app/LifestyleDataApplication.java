package com.example.lifestyle_data_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LifestyleDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifestyleDataApplication.class, args);
	}

}
