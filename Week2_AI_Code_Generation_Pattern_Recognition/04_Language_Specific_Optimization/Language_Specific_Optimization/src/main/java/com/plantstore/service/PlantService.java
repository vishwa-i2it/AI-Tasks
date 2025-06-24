package com.plantstore.service;

import com.plantstore.entity.Plant;
import com.plantstore.exception.InsufficientStockException;
import com.plantstore.exception.ResourceNotFoundException;
import com.plantstore.repository.PlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Plant entity operations.
 * 
 * <p>This service provides business logic for plant management including
 * CRUD operations, stock management, and search functionality.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Service
@Transactional
public class PlantService {

    private static final Logger logger = LoggerFactory.getLogger(PlantService.class);

    private final PlantRepository plantRepository;

    @Value("${plant-store.inventory.low-stock-threshold:10}")
    private int lowStockThreshold;

    @Value("${plant-store.inventory.out-of-stock-threshold:0}")
    private int outOfStockThreshold;

    /**
     * Constructor with dependency injection.
     * 
     * @param plantRepository the plant repository
     */
    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    /**
     * Creates a new plant.
     * 
     * @param plant the plant to create
     * @return the created plant
     */
    public Plant createPlant(Plant plant) {
        logger.info("Creating new plant: {}", plant.getName());
        
        if (plantRepository.existsByName(plant.getName())) {
            throw new IllegalArgumentException("Plant with name '" + plant.getName() + "' already exists");
        }
        
        Plant savedPlant = plantRepository.save(plant);
        logger.info("Successfully created plant with ID: {}", savedPlant.getId());
        return savedPlant;
    }

    /**
     * Retrieves a plant by ID.
     * 
     * @param id the plant ID
     * @return the plant if found
     * @throws ResourceNotFoundException if plant is not found
     */
    @Transactional(readOnly = true)
    public Plant getPlantById(Long id) {
        logger.debug("Retrieving plant with ID: {}", id);
        
        return plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", id));
    }

    /**
     * Retrieves a plant by name.
     * 
     * @param name the plant name
     * @return the plant if found
     * @throws ResourceNotFoundException if plant is not found
     */
    @Transactional(readOnly = true)
    public Plant getPlantByName(String name) {
        logger.debug("Retrieving plant with name: {}", name);
        
        return plantRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", "name", name));
    }

    /**
     * Updates an existing plant.
     * 
     * @param id the plant ID
     * @param plantDetails the updated plant details
     * @return the updated plant
     * @throws ResourceNotFoundException if plant is not found
     */
    public Plant updatePlant(Long id, Plant plantDetails) {
        logger.info("Updating plant with ID: {}", id);
        
        Plant plant = getPlantById(id);
        
        // Update fields
        plant.setName(plantDetails.getName());
        plant.setDescription(plantDetails.getDescription());
        plant.setPrice(plantDetails.getPrice());
        plant.setStockQuantity(plantDetails.getStockQuantity());
        plant.setImageUrl(plantDetails.getImageUrl());
        plant.setCareLevel(plantDetails.getCareLevel());
        plant.setLightRequirement(plantDetails.getLightRequirement());
        plant.setWaterFrequencyDays(plantDetails.getWaterFrequencyDays());
        plant.setMatureHeightCm(plantDetails.getMatureHeightCm());
        plant.setIsIndoor(plantDetails.getIsIndoor());
        plant.setIsPetFriendly(plantDetails.getIsPetFriendly());
        plant.setCategory(plantDetails.getCategory());
        
        Plant updatedPlant = plantRepository.save(plant);
        logger.info("Successfully updated plant with ID: {}", updatedPlant.getId());
        return updatedPlant;
    }

    /**
     * Deletes a plant by ID.
     * 
     * @param id the plant ID
     * @throws ResourceNotFoundException if plant is not found
     */
    public void deletePlant(Long id) {
        logger.info("Deleting plant with ID: {}", id);
        
        if (!plantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plant", id);
        }
        
        plantRepository.deleteById(id);
        logger.info("Successfully deleted plant with ID: {}", id);
    }

    /**
     * Retrieves all plants with pagination.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortDir the sort direction
     * @return a page of plants
     */
    @Transactional(readOnly = true)
    public Page<Plant> getAllPlants(int page, int size, String sortBy, String sortDir) {
        logger.debug("Retrieving all plants with pagination - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return plantRepository.findAll(pageable);
    }

    /**
     * Searches plants by name or description.
     * 
     * @param searchTerm the search term
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of matching plants
     */
    @Transactional(readOnly = true)
    public Page<Plant> searchPlants(String searchTerm, int page, int size) {
        logger.debug("Searching plants with term: '{}'", searchTerm);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByNameOrDescriptionContainingIgnoreCase(searchTerm, pageable);
    }

    /**
     * Finds plants by category.
     * 
     * @param categoryId the category ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of plants in the category
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsByCategory(Long categoryId, int page, int size) {
        logger.debug("Retrieving plants for category ID: {}", categoryId);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Finds plants by care level.
     * 
     * @param careLevel the care level
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of plants with the specified care level
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsByCareLevel(Plant.CareLevel careLevel, int page, int size) {
        logger.debug("Retrieving plants with care level: {}", careLevel);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByCareLevel(careLevel, pageable);
    }

    /**
     * Finds plants by light requirement.
     * 
     * @param lightRequirement the light requirement
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of plants with the specified light requirement
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsByLightRequirement(Plant.LightRequirement lightRequirement, int page, int size) {
        logger.debug("Retrieving plants with light requirement: {}", lightRequirement);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByLightRequirement(lightRequirement, pageable);
    }

    /**
     * Finds indoor or outdoor plants.
     * 
     * @param isIndoor whether the plant is indoor
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of indoor/outdoor plants
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsByIndoorStatus(Boolean isIndoor, int page, int size) {
        logger.debug("Retrieving plants with indoor status: {}", isIndoor);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByIsIndoor(isIndoor, pageable);
    }

    /**
     * Finds pet-friendly plants.
     * 
     * @param isPetFriendly whether the plant is pet-friendly
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of pet-friendly plants
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsByPetFriendlyStatus(Boolean isPetFriendly, int page, int size) {
        logger.debug("Retrieving plants with pet-friendly status: {}", isPetFriendly);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByIsPetFriendly(isPetFriendly, pageable);
    }

    /**
     * Finds plants within a price range.
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of plants within the price range
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        logger.debug("Retrieving plants with price range: {} - {}", minPrice, maxPrice);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    /**
     * Finds plants in stock.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of plants that are in stock
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsInStock(int page, int size) {
        logger.debug("Retrieving plants in stock");
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByStockQuantityGreaterThan(outOfStockThreshold, pageable);
    }

    /**
     * Finds plants with low stock.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of plants with low stock
     */
    @Transactional(readOnly = true)
    public Page<Plant> getPlantsWithLowStock(int page, int size) {
        logger.debug("Retrieving plants with low stock (threshold: {})", lowStockThreshold);
        
        Pageable pageable = PageRequest.of(page, size);
        return plantRepository.findByStockQuantityLessThanEqual(lowStockThreshold, pageable);
    }

    /**
     * Reduces stock for a plant.
     * 
     * @param plantId the plant ID
     * @param quantity the quantity to reduce
     * @throws ResourceNotFoundException if plant is not found
     * @throws InsufficientStockException if insufficient stock
     */
    public void reduceStock(Long plantId, Integer quantity) {
        logger.info("Reducing stock for plant ID: {} by quantity: {}", plantId, quantity);
        
        Plant plant = getPlantById(plantId);
        
        if (plant.getStockQuantity() < quantity) {
            throw new InsufficientStockException(plantId, plant.getName(), quantity, plant.getStockQuantity());
        }
        
        plant.reduceStock(quantity);
        plantRepository.save(plant);
        logger.info("Successfully reduced stock for plant ID: {}. New stock: {}", 
                   plantId, plant.getStockQuantity());
    }

    /**
     * Adds stock for a plant.
     * 
     * @param plantId the plant ID
     * @param quantity the quantity to add
     * @throws ResourceNotFoundException if plant is not found
     */
    public void addStock(Long plantId, Integer quantity) {
        logger.info("Adding stock for plant ID: {} by quantity: {}", plantId, quantity);
        
        Plant plant = getPlantById(plantId);
        plant.addStock(quantity);
        plantRepository.save(plant);
        logger.info("Successfully added stock for plant ID: {}. New stock: {}", 
                   plantId, plant.getStockQuantity());
    }

    /**
     * Gets the most expensive plant.
     * 
     * @return the most expensive plant if found
     */
    @Transactional(readOnly = true)
    public Optional<Plant> getMostExpensivePlant() {
        logger.debug("Retrieving most expensive plant");
        return plantRepository.findMostExpensivePlant();
    }

    /**
     * Gets the least expensive plant.
     * 
     * @return the least expensive plant if found
     */
    @Transactional(readOnly = true)
    public Optional<Plant> getLeastExpensivePlant() {
        logger.debug("Retrieving least expensive plant");
        return plantRepository.findLeastExpensivePlant();
    }

    /**
     * Gets plants with the highest stock quantities.
     * 
     * @param limit the maximum number of results
     * @return a list of plants with the highest stock quantities
     */
    @Transactional(readOnly = true)
    public List<Plant> getTopPlantsByStockQuantity(int limit) {
        logger.debug("Retrieving top {} plants by stock quantity", limit);
        
        Pageable pageable = PageRequest.of(0, limit);
        return plantRepository.findTopPlantsByStockQuantity(pageable);
    }

    /**
     * Checks if a plant exists by name.
     * 
     * @param name the plant name
     * @return true if plant exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean plantExistsByName(String name) {
        return plantRepository.existsByName(name);
    }

    /**
     * Counts plants by category.
     * 
     * @param categoryId the category ID
     * @return the number of plants in the category
     */
    @Transactional(readOnly = true)
    public long countPlantsByCategory(Long categoryId) {
        return plantRepository.countByCategoryId(categoryId);
    }
} 