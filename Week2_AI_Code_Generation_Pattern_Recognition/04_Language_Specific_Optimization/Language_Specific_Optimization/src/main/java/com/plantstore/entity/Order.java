package com.plantstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a customer order in the e-commerce system.
 * 
 * <p>This entity contains order information including items, total amount,
 * shipping details, and order status.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user", columnList = "user_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_created", columnList = "created_at")
})
public class Order extends BaseEntity {

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Positive(message = "Subtotal must be positive")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Positive(message = "Tax amount must be positive")
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Positive(message = "Shipping cost must be positive")
    @Column(name = "shipping_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Positive(message = "Total amount must be positive")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    /**
     * Enum representing the status of an order.
     */
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    }

    /**
     * Default constructor.
     */
    public Order() {
    }

    /**
     * Constructor with user.
     * 
     * @param user the user placing the order
     */
    public Order(User user) {
        this.user = user;
    }

    /**
     * Adds an item to the order.
     * 
     * @param orderItem the order item to add
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        recalculateTotals();
    }

    /**
     * Removes an item from the order.
     * 
     * @param orderItem the order item to remove
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        recalculateTotals();
    }

    /**
     * Recalculates the order totals based on items.
     */
    public void recalculateTotals() {
        this.subtotal = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate tax (assuming 8.5% tax rate)
        this.taxAmount = subtotal.multiply(new BigDecimal("0.085"));
        
        // Calculate total
        this.totalAmount = subtotal.add(taxAmount).add(shippingCost);
    }

    /**
     * Gets the number of items in the order.
     * 
     * @return the number of items
     */
    public int getItemCount() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * Checks if the order can be cancelled.
     * 
     * @return true if order can be cancelled, false otherwise
     */
    public boolean canBeCancelled() {
        return OrderStatus.PENDING.equals(status) || OrderStatus.CONFIRMED.equals(status);
    }

    /**
     * Checks if the order has been shipped.
     * 
     * @return true if order has been shipped, false otherwise
     */
    public boolean isShipped() {
        return OrderStatus.SHIPPED.equals(status) || OrderStatus.DELIVERED.equals(status);
    }

    /**
     * Checks if the order is completed.
     * 
     * @return true if order is completed, false otherwise
     */
    public boolean isCompleted() {
        return OrderStatus.DELIVERED.equals(status);
    }

    // Getters and Setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + getId() +
                ", userId=" + (user != null ? user.getId() : null) +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + getItemCount() +
                '}';
    }
} 