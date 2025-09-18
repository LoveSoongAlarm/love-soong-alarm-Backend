package com.lovesoongalarm.lovesoongalarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@ConfigurationPropertiesScan("com.lovesoongalarm.lovesoongalarm.domain.pay.config")
public class LovesoongalarmApplication {

	public static void main(String[] args) {
		SpringApplication.run(LovesoongalarmApplication.class, args);
	}

}
