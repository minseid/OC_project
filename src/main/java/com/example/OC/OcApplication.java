package com.example.OC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.example.OC")

public class OcApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcApplication.class, args);
	}

}
