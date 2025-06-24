package com.plantsales.repository;

import com.plantsales.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    
    Optional<Plant> findByName(String name);
    
    List<Plant> findByCategoryId(Long categoryId);
    
    List<Plant> findByIsActiveTrue();
    
    List<Plant> findByCareLevel(Plant.CareLevel careLevel);
    
    List<Plant> findByLightRequirement(Plant.LightRequirement lightRequirement);
    
    List<Plant> findByIsPetFriendlyTrue();
    
    List<Plant> findByIsIndoorTrue();
    
    List<Plant> findByIsOutdoorTrue();
    
    List<Plant> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Plant> findByStockQuantityLessThanEqual(Integer quantity);
    
    @Query("SELECT p FROM Plant p WHERE p.stockQuantity <= p.minStockLevel")
    List<Plant> findLowStockPlants();
    
    @Query("SELECT p FROM Plant p WHERE p.name LIKE %:keyword% OR p.scientificName LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Plant> searchPlants(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Plant p WHERE p.isActive = true AND p.stockQuantity > 0")
    List<Plant> findAvailablePlants();
} 