package com.saccolite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for Sahihi. Spring Boot auto-scans this package (com.saccolite)
 * and everything under it - all our @Service, @Repository, @RestController,
 * and @Configuration classes get picked up automatically, no manual wiring needed.
 */
@SpringBootApplication
public class SahihiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SahihiApplication.class, args);
    }
}