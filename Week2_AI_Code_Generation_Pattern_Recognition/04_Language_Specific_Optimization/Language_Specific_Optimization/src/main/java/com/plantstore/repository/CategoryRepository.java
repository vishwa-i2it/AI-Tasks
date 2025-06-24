package com.plantstore.repository;

import com.plantstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity operations.
 * 
 * <p>This interface provides data access methods for Category entities
 * including custom queries for common business operations.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by name.
     * 
     * @param name the category name
     * @return an optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Finds categories by name containing the specified text (case-insensitive).
     * 
     * @param name the name to search for
     * @param pageable pagination information
     * @return a page of categories matching the name
     */
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Finds categories by description containing the specified text (case-insensitive).
     * 
     * @param description the description to search for
     * @param pageable pagination information
     * @return a page of categories matching the description
     */
    Page<Category> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * Finds categories by name or description containing the specified text (case-insensitive).
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of categories matching the search term
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Category> findByNameOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm, 
                                                              Pageable pageable);

    /**
     * Checks if a category exists by name.
     * 
     * @param name the category name
     * @return true if a category with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds categories that have plants.
     * 
     * @param pageable pagination information
     * @return a page of categories that have at least one plant
     */
    @Query("SELECT c FROM Category c WHERE SIZE(c.plants) > 0")
    Page<Category> findCategoriesWithPlants(Pageable pageable);

    /**
     * Finds categories that have no plants.
     * 
     * @param pageable pagination information
     * @return a page of categories that have no plants
     */
    @Query("SELECT c FROM Category c WHERE SIZE(c.plants) = 0")
    Page<Category> findEmptyCategories(Pageable pageable);

    /**
     * Finds categories ordered by the number of plants they contain (descending).
     * 
     * @param pageable pagination information
     * @return a page of categories ordered by plant count
     */
    @Query("SELECT c FROM Category c ORDER BY SIZE(c.plants) DESC")
    Page<Category> findCategoriesOrderedByPlantCount(Pageable pageable);

    /**
     * Finds categories with a minimum number of plants.
     * 
     * @param minPlantCount the minimum number of plants
     * @param pageable pagination information
     * @return a page of categories with at least the specified number of plants
     */
    @Query("SELECT c FROM Category c WHERE SIZE(c.plants) >= :minPlantCount")
    Page<Category> findCategoriesWithMinimumPlantCount(@Param("minPlantCount") int minPlantCount, 
                                                      Pageable pageable);

    /**
     * Finds all category names.
     * 
     * @return a list of all category names
     */
    @Query("SELECT c.name FROM Category c")
    List<String> findAllCategoryNames();

    /**
     * Counts the number of plants in a category.
     * 
     * @param categoryId the category ID
     * @return the number of plants in the category
     */
    @Query("SELECT SIZE(c.plants) FROM Category c WHERE c.id = :categoryId")
    int countPlantsInCategory(@Param("categoryId") Long categoryId);

    /**
     * Finds categories that are similar to the given name.
     * 
     * @param name the category name to find similar categories for
     * @param pageable pagination information
     * @return a page of categories similar to the given name
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(:name) LIKE LOWER(CONCAT('%', c.name, '%'))")
    Page<Category> findSimilarCategories(@Param("name") String name, Pageable pageable);
} 