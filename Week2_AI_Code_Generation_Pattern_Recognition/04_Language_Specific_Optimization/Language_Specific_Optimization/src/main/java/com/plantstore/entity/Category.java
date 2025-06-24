package com.plantstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a plant category in the e-commerce system.
 * 
 * <p>This entity is used to organize plants into logical groups
 * such as "Indoor Plants", "Succulents", "Flowering Plants", etc.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_name", columnList = "name")
})
public class Category extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 200, message = "Image URL cannot exceed 200 characters")
    @Column(name = "image_url", length = 200)
    private String imageUrl;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plant> plants = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Category() {
    }

    /**
     * Constructor with category name.
     * 
     * @param name the category name
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Constructor with category name and description.
     * 
     * @param name the category name
     * @param description the category description
     */
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the number of plants in this category.
     * 
     * @return the number of plants
     */
    public int getPlantCount() {
        return plants.size();
    }

    /**
     * Adds a plant to this category.
     * 
     * @param plant the plant to add
     */
    public void addPlant(Plant plant) {
        plants.add(plant);
        plant.setCategory(this);
    }

    /**
     * Removes a plant from this category.
     * 
     * @param plant the plant to remove
     */
    public void removePlant(Plant plant) {
        plants.remove(plant);
        plant.setCategory(null);
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", plantCount=" + getPlantCount() +
                '}';
    }
} 