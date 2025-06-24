package com.plantstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Entity representing an individual item within an order.
 * 
 * <p>This entity contains information about a specific plant item
 * that was ordered, including quantity and price at the time of purchase.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_plant", columnList = "plant_id")
})
public class OrderItem extends BaseEntity {

    @NotNull(message = "Order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull(message = "Plant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Default constructor.
     */
    public OrderItem() {
    }

    /**
     * Constructor with order, plant, quantity, and price.
     * 
     * @param order the order this item belongs to
     * @param plant the plant being ordered
     * @param quantity the quantity ordered
     * @param price the price per unit at the time of order
     */
    public OrderItem(Order order, Plant plant, Integer quantity, BigDecimal price) {
        this.order = order;
        this.plant = plant;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Gets the total price for this order item.
     * 
     * @return the total price (price * quantity)
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Checks if this item has sufficient stock available.
     * 
     * @return true if sufficient stock is available, false otherwise
     */
    public boolean hasSufficientStock() {
        return plant != null && plant.getStockQuantity() >= quantity;
    }

    /**
     * Gets the plant name for display purposes.
     * 
     * @return the plant name or null if plant is not set
     */
    public String getPlantName() {
        return plant != null ? plant.getName() : null;
    }

    /**
     * Gets the plant image URL for display purposes.
     * 
     * @return the plant image URL or null if plant is not set
     */
    public String getPlantImageUrl() {
        return plant != null ? plant.getImageUrl() : null;
    }

    // Getters and Setters

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + getId() +
                ", orderId=" + (order != null ? order.getId() : null) +
                ", plantId=" + (plant != null ? plant.getId() : null) +
                ", plantName='" + getPlantName() + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
} 