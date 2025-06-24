package com.plantstore.repository;

import com.plantstore.entity.Plant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Plant entity operations.
 * 
 * <p>This interface provides data access methods for Plant entities
 * including custom queries for common business operations.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {

    /**
     * Finds plants by category ID.
     * 
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return a page of plants in the specified category
     */
    Page<Plant> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Finds plants by care level.
     * 
     * @param careLevel the care level
     * @param pageable pagination information
     * @return a page of plants with the specified care level
     */
    Page<Plant> findByCareLevel(Plant.CareLevel careLevel, Pageable pageable);

    /**
     * Finds plants by light requirement.
     * 
     * @param lightRequirement the light requirement
     * @param pageable pagination information
     * @return a page of plants with the specified light requirement
     */
    Page<Plant> findByLightRequirement(Plant.LightRequirement lightRequirement, Pageable pageable);

    /**
     * Finds indoor plants.
     * 
     * @param isIndoor whether the plant is indoor
     * @param pageable pagination information
     * @return a page of indoor/outdoor plants
     */
    Page<Plant> findByIsIndoor(Boolean isIndoor, Pageable pageable);

    /**
     * Finds pet-friendly plants.
     * 
     * @param isPetFriendly whether the plant is pet-friendly
     * @param pageable pagination information
     * @return a page of pet-friendly plants
     */
    Page<Plant> findByIsPetFriendly(Boolean isPetFriendly, Pageable pageable);

    /**
     * Finds plants in stock (stock quantity > 0).
     * 
     * @param pageable pagination information
     * @return a page of plants that are in stock
     */
    Page<Plant> findByStockQuantityGreaterThan(Integer stockQuantity, Pageable pageable);

    /**
     * Finds plants with low stock (stock quantity <= threshold).
     * 
     * @param threshold the low stock threshold
     * @param pageable pagination information
     * @return a page of plants with low stock
     */
    Page<Plant> findByStockQuantityLessThanEqual(Integer threshold, Pageable pageable);

    /**
     * Finds plants within a price range.
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return a page of plants within the price range
     */
    Page<Plant> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Finds plants by name containing the specified text (case-insensitive).
     * 
     * @param name the name to search for
     * @param pageable pagination information
     * @return a page of plants matching the name
     */
    Page<Plant> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Finds plants by description containing the specified text (case-insensitive).
     * 
     * @param description the description to search for
     * @param pageable pagination information
     * @return a page of plants matching the description
     */
    Page<Plant> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * Finds plants by name or description containing the specified text (case-insensitive).
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of plants matching the search term
     */
    @Query("SELECT p FROM Plant p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Plant> findByNameOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm, 
                                                           Pageable pageable);

    /**
     * Finds plants that are out of stock.
     * 
     * @return a list of plants that are out of stock
     */
    List<Plant> findByStockQuantity(Integer stockQuantity);

    /**
     * Finds plants by category name.
     * 
     * @param categoryName the category name
     * @param pageable pagination information
     * @return a page of plants in the specified category
     */
    @Query("SELECT p FROM Plant p JOIN p.category c WHERE c.name = :categoryName")
    Page<Plant> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    /**
     * Counts plants by category.
     * 
     * @param categoryId the category ID
     * @return the number of plants in the category
     */
    long countByCategoryId(Long categoryId);

    /**
     * Finds the most expensive plant.
     * 
     * @return an optional containing the most expensive plant
     */
    @Query("SELECT p FROM Plant p WHERE p.price = (SELECT MAX(p2.price) FROM Plant p2)")
    Optional<Plant> findMostExpensivePlant();

    /**
     * Finds the least expensive plant.
     * 
     * @return an optional containing the least expensive plant
     */
    @Query("SELECT p FROM Plant p WHERE p.price = (SELECT MIN(p2.price) FROM Plant p2)")
    Optional<Plant> findLeastExpensivePlant();

    /**
     * Finds plants with the highest stock quantity.
     * 
     * @param limit the maximum number of results
     * @return a list of plants with the highest stock quantities
     */
    @Query("SELECT p FROM Plant p ORDER BY p.stockQuantity DESC")
    List<Plant> findTopPlantsByStockQuantity(Pageable pageable);

    /**
     * Checks if a plant exists by name.
     * 
     * @param name the plant name
     * @return true if a plant with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a plant by name.
     * 
     * @param name the plant name
     * @return an optional containing the plant if found
     */
    Optional<Plant> findByName(String name);
} 