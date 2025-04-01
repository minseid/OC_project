package com.where;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.where")
@EnableJpaAuditing
public class OcApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcApplication.class, args);
	}

}
