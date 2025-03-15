package com.example.Sparta_Store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableRetry
public class SpartaStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpartaStoreApplication.class, args);
	}

}
