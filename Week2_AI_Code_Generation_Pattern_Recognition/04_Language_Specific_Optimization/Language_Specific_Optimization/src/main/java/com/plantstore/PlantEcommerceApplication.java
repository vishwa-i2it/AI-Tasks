package com.plantstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the Plant E-commerce system.
 * 
 * <p>This class serves as the entry point for the Spring Boot application
 * and configures various aspects of the application including JPA auditing,
 * transaction management, and asynchronous processing.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
public class PlantEcommerceApplication {

    /**
     * Main method to start the Plant E-commerce application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PlantEcommerceApplication.class, args);
    }
} 