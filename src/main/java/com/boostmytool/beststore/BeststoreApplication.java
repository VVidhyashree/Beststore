package com.boostmytool.beststore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@SpringBootApplication
@ComponentScan("com.boostmytool.beststore")
@EnableJpaRepositories("com.boostmytool.beststore.services")
@EntityScan("com.boostmytool.beststore.models")

public class BeststoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeststoreApplication.class, args);
	}

}
