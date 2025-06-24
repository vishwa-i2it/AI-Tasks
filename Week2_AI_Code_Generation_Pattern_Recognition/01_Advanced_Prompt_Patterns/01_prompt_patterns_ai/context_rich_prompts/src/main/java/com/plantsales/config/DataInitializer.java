package com.plantsales.config;

import com.plantsales.model.*;
import com.plantsales.repository.PlantRepository;
import com.plantsales.repository.PlantCategoryRepository;
import com.plantsales.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PlantRepository plantRepository;
    
    @Autowired
    private PlantCategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample data
        initializeCategories();
        initializePlants();
        initializeUsers();
    }

    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            PlantCategory indoor = new PlantCategory("Indoor Plants", "Perfect for home and office environments");
            PlantCategory outdoor = new PlantCategory("Outdoor Plants", "Great for gardens and landscapes");
            PlantCategory succulents = new PlantCategory("Succulents", "Low-maintenance drought-resistant plants");
            PlantCategory herbs = new PlantCategory("Herbs", "Culinary and medicinal herbs");
            
            categoryRepository.saveAll(Arrays.asList(indoor, outdoor, succulents, herbs));
        }
    }

    private void initializePlants() {
        if (plantRepository.count() == 0) {
            PlantCategory indoor = categoryRepository.findByName("Indoor Plants").orElse(null);
            PlantCategory succulents = categoryRepository.findByName("Succulents").orElse(null);
            
            Plant snakePlant = new Plant();
            snakePlant.setCategory(indoor);
            snakePlant.setCareLevel(Plant.CareLevel.EASY);
            snakePlant.setLightRequirement(Plant.LightRequirement.LOW);
            snakePlant.setIsPetFriendly(false);
            snakePlant.setCareInstructions("Water sparingly, allow soil to dry between waterings");
            
            Plant aloeVera = new Plant();
            aloeVera.setCategory(succulents);
            aloeVera.setCareLevel(Plant.CareLevel.EASY);
            aloeVera.setLightRequirement(Plant.LightRequirement.MEDIUM);
            aloeVera.setIsPetFriendly(false);
            aloeVera.setCareInstructions("Bright indirect light, water when soil is dry");
            
            plantRepository.saveAll(Arrays.asList(snakePlant, aloeVera));
        }
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", "admin@plantsales.com", 
                passwordEncoder.encode("admin123"), "Admin", "User");
            admin.setRole(User.Role.ADMIN);
            admin.setIsEmailVerified(true);
            
            User customer = new User("customer", "customer@example.com", 
                passwordEncoder.encode("customer123"), "John", "Doe");
            customer.setRole(User.Role.CUSTOMER);
            customer.setIsEmailVerified(true);
            
            userRepository.saveAll(Arrays.asList(admin, customer));
        }
    }
} 