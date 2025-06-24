package com.plantstore;

import com.plantstore.entity.Plant;
import com.plantstore.repository.PlantRepository;
import com.plantstore.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlantServiceTest {
    @Mock
    private PlantRepository plantRepository;

    @InjectMocks
    private PlantService plantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPlant_Success() {
        Plant plant = new Plant();
        plant.setName("Aloe Vera");
        plant.setPrice(10.0);
        plant.setStockQuantity(100);
        when(plantRepository.save(any(Plant.class))).thenReturn(plant);

        Plant saved = plantService.addPlant(plant);
        assertNotNull(saved);
        assertEquals("Aloe Vera", saved.getName());
    }

    @Test
    void testGetPlantById_NotFound() {
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());
        Plant result = plantService.getPlantById(1L);
        assertNull(result);
    }
} 