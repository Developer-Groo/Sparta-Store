package com.example.Sparta_Store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpartaStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpartaStoreApplication.class, args);
	}

}
