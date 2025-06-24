package com.plantsales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart shoppingCart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "plant_name")
    private String plantName;
    
    @Column(name = "plant_price", precision = 10, scale = 2)
    private BigDecimal plantPrice;
    
    @Column(name = "plant_image_url")
    private String plantImageUrl;
    
    // Constructors
    public CartItem() {}
    
    public CartItem(Plant plant, Integer quantity) {
        this.plant = plant;
        this.quantity = quantity;
        this.plantName = plant.getName();
        this.plantPrice = plant.getPrice();
        this.plantImageUrl = plant.getImageUrl();
    }
    
    // Business methods
    public BigDecimal getTotalPrice() {
        return plantPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
    }
    
    public void incrementQuantity() {
        this.quantity++;
    }
    
    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ShoppingCart getShoppingCart() { return shoppingCart; }
    public void setShoppingCart(ShoppingCart shoppingCart) { this.shoppingCart = shoppingCart; }
    
    public Plant getPlant() { return plant; }
    public void setPlant(Plant plant) { this.plant = plant; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public String getPlantName() { return plantName; }
    public void setPlantName(String plantName) { this.plantName = plantName; }
    
    public BigDecimal getPlantPrice() { return plantPrice; }
    public void setPlantPrice(BigDecimal plantPrice) { this.plantPrice = plantPrice; }
    
    public String getPlantImageUrl() { return plantImageUrl; }
    public void setPlantImageUrl(String plantImageUrl) { this.plantImageUrl = plantImageUrl; }
} 