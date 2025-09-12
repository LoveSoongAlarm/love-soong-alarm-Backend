package com.lovesoongalarm.lovesoongalarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LovesoongalarmApplication {

	public static void main(String[] args) {
		SpringApplication.run(LovesoongalarmApplication.class, args);
	}

}
