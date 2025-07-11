package com.example.Licoreria_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class LicoreriaBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(LicoreriaBackendApplication.class, args);
	}
}
