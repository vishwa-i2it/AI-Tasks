package com.plantsales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plants")
public class Plant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Plant name is required")
    @Size(max = 100, message = "Plant name cannot exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String name;
    
    @NotBlank(message = "Scientific name is required")
    @Size(max = 100, message = "Scientific name cannot exceed 100 characters")
    @Column(name = "scientific_name", nullable = false)
    private String scientificName;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Column(name = "min_stock_level")
    private Integer minStockLevel = 5;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "care_level", nullable = false)
    private CareLevel careLevel = CareLevel.EASY;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "light_requirement", nullable = false)
    private LightRequirement lightRequirement = LightRequirement.MEDIUM;
    
    @Column(name = "water_frequency_days")
    private Integer waterFrequencyDays = 7;
    
    @Column(name = "fertilizer_frequency_days")
    private Integer fertilizerFrequencyDays = 30;
    
    @Size(max = 1000, message = "Care instructions cannot exceed 1000 characters")
    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;
    
    @Column(name = "max_height_cm")
    private Integer maxHeightCm;
    
    @Column(name = "max_width_cm")
    private Integer maxWidthCm;
    
    @Column(name = "pot_size_cm")
    private Integer potSizeCm;
    
    @Column(name = "is_pet_friendly")
    private Boolean isPetFriendly = false;
    
    @Column(name = "is_indoor")
    private Boolean isIndoor = true;
    
    @Column(name = "is_outdoor")
    private Boolean isOutdoor = false;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();
    
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PlantCategory category;
    
    // Enums
    public enum CareLevel {
        EASY, MEDIUM, HARD, EXPERT
    }
    
    public enum LightRequirement {
        LOW, MEDIUM, HIGH, DIRECT_SUN
    }
    
    // Constructors
    public Plant() {}
    
    public Plant(String name, String scientificName, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.scientificName = scientificName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = LocalDateTime.now();
    }
    
    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isInStock() {
        return stockQuantity > 0;
    }
    
    public boolean isLowStock() {
        return stockQuantity <= minStockLevel;
    }
    
    public void decreaseStock(int quantity) {
        if (this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
        } else {
            throw new IllegalStateException("Insufficient stock");
        }
    }
    
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public CareLevel getCareLevel() { return careLevel; }
    public void setCareLevel(CareLevel careLevel) { this.careLevel = careLevel; }
    
    public LightRequirement getLightRequirement() { return lightRequirement; }
    public void setLightRequirement(LightRequirement lightRequirement) { this.lightRequirement = lightRequirement; }
    
    public Integer getWaterFrequencyDays() { return waterFrequencyDays; }
    public void setWaterFrequencyDays(Integer waterFrequencyDays) { this.waterFrequencyDays = waterFrequencyDays; }
    
    public Integer getFertilizerFrequencyDays() { return fertilizerFrequencyDays; }
    public void setFertilizerFrequencyDays(Integer fertilizerFrequencyDays) { this.fertilizerFrequencyDays = fertilizerFrequencyDays; }
    
    public String getCareInstructions() { return careInstructions; }
    public void setCareInstructions(String careInstructions) { this.careInstructions = careInstructions; }
    
    public Integer getMaxHeightCm() { return maxHeightCm; }
    public void setMaxHeightCm(Integer maxHeightCm) { this.maxHeightCm = maxHeightCm; }
    
    public Integer getMaxWidthCm() { return maxWidthCm; }
    public void setMaxWidthCm(Integer maxWidthCm) { this.maxWidthCm = maxWidthCm; }
    
    public Integer getPotSizeCm() { return potSizeCm; }
    public void setPotSizeCm(Integer potSizeCm) { this.potSizeCm = potSizeCm; }
    
    public Boolean getIsPetFriendly() { return isPetFriendly; }
    public void setIsPetFriendly(Boolean isPetFriendly) { this.isPetFriendly = isPetFriendly; }
    
    public Boolean getIsIndoor() { return isIndoor; }
    public void setIsIndoor(Boolean isIndoor) { this.isIndoor = isIndoor; }
    
    public Boolean getIsOutdoor() { return isOutdoor; }
    public void setIsOutdoor(Boolean isOutdoor) { this.isOutdoor = isOutdoor; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<Review> getReviews() { return reviews; }
    public void setReviews(Set<Review> reviews) { this.reviews = reviews; }
    
    public Set<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(Set<OrderItem> orderItems) { this.orderItems = orderItems; }
    
    public PlantCategory getCategory() { return category; }
    public void setCategory(PlantCategory category) { this.category = category; }
} 