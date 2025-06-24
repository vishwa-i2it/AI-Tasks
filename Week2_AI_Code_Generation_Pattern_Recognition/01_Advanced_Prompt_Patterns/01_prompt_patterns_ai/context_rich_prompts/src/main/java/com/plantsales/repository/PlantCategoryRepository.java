package com.plantsales.repository;

import com.plantsales.model.PlantCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantCategoryRepository extends JpaRepository<PlantCategory, Long> {
    
    Optional<PlantCategory> findByName(String name);
    
    List<PlantCategory> findByIsActiveTrue();
    
    boolean existsByName(String name);
}