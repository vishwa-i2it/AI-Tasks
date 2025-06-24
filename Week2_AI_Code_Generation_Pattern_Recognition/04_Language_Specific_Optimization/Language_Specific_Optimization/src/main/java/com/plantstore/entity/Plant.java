package com.plantstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a plant in the e-commerce system.
 * 
 * <p>This entity contains all the necessary information about a plant including
 * its name, description, price, stock quantity, and category information.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "plants", indexes = {
    @Index(name = "idx_plant_name", columnList = "name"),
    @Index(name = "idx_plant_category", columnList = "category_id"),
    @Index(name = "idx_plant_price", columnList = "price")
})
public class Plant extends BaseEntity {

    @NotBlank(message = "Plant name is required")
    @Size(min = 2, max = 100, message = "Plant name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 digits and 2 decimal places")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Size(max = 200, message = "Image URL cannot exceed 200 characters")
    @Column(name = "image_url", length = 200)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "care_level", length = 20)
    private CareLevel careLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "light_requirement", length = 20)
    private LightRequirement lightRequirement;

    @Column(name = "water_frequency_days")
    @Min(value = 1, message = "Water frequency must be at least 1 day")
    private Integer waterFrequencyDays;

    @Column(name = "mature_height_cm")
    @Min(value = 0, message = "Mature height cannot be negative")
    private Integer matureHeightCm;

    @Column(name = "is_indoor", nullable = false)
    private Boolean isIndoor = true;

    @Column(name = "is_pet_friendly")
    private Boolean isPetFriendly = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    /**
     * Enum representing the care level required for a plant.
     */
    public enum CareLevel {
        EASY, MEDIUM, HARD, EXPERT
    }

    /**
     * Enum representing the light requirement for a plant.
     */
    public enum LightRequirement {
        LOW, MEDIUM, HIGH, DIRECT_SUNLIGHT
    }

    /**
     * Default constructor.
     */
    public Plant() {
    }

    /**
     * Constructor with basic plant information.
     * 
     * @param name the plant name
     * @param price the plant price
     * @param stockQuantity the stock quantity
     */
    public Plant(String name, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Checks if the plant is in stock.
     * 
     * @return true if stock quantity is greater than 0, false otherwise
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Checks if the plant has low stock.
     * 
     * @return true if stock quantity is 10 or less, false otherwise
     */
    public boolean hasLowStock() {
        return stockQuantity <= 10;
    }

    /**
     * Reduces the stock quantity by the specified amount.
     * 
     * @param quantity the quantity to reduce
     * @throws IllegalArgumentException if quantity is negative or exceeds available stock
     */
    public void reduceStock(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Increases the stock quantity by the specified amount.
     * 
     * @param quantity the quantity to add
     * @throws IllegalArgumentException if quantity is negative
     */
    public void addStock(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.stockQuantity += quantity;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CareLevel getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(CareLevel careLevel) {
        this.careLevel = careLevel;
    }

    public LightRequirement getLightRequirement() {
        return lightRequirement;
    }

    public void setLightRequirement(LightRequirement lightRequirement) {
        this.lightRequirement = lightRequirement;
    }

    public Integer getWaterFrequencyDays() {
        return waterFrequencyDays;
    }

    public void setWaterFrequencyDays(Integer waterFrequencyDays) {
        this.waterFrequencyDays = waterFrequencyDays;
    }

    public Integer getMatureHeightCm() {
        return matureHeightCm;
    }

    public void setMatureHeightCm(Integer matureHeightCm) {
        this.matureHeightCm = matureHeightCm;
    }

    public Boolean getIsIndoor() {
        return isIndoor;
    }

    public void setIsIndoor(Boolean isIndoor) {
        this.isIndoor = isIndoor;
    }

    public Boolean getIsPetFriendly() {
        return isPetFriendly;
    }

    public void setIsPetFriendly(Boolean isPetFriendly) {
        this.isPetFriendly = isPetFriendly;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", careLevel=" + careLevel +
                ", isIndoor=" + isIndoor +
                ", isPetFriendly=" + isPetFriendly +
                '}';
    }
} 