package com.plantstore.repository;

import com.plantstore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity operations.
 * 
 * <p>This interface provides data access methods for Order entities
 * including custom queries for common business operations.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds orders by user ID.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return a page of orders for the specified user
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds orders by status.
     * 
     * @param status the order status
     * @param pageable pagination information
     * @return a page of orders with the specified status
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * Finds orders by user ID and status.
     * 
     * @param userId the user ID
     * @param status the order status
     * @param pageable pagination information
     * @return a page of orders for the specified user and status
     */
    Page<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable);

    /**
     * Finds orders created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return a page of orders created within the date range
     */
    Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Finds orders with total amount greater than the specified value.
     * 
     * @param amount the minimum amount
     * @param pageable pagination information
     * @return a page of orders with total amount greater than the specified value
     */
    Page<Order> findByTotalAmountGreaterThan(BigDecimal amount, Pageable pageable);

    /**
     * Finds orders with total amount less than the specified value.
     * 
     * @param amount the maximum amount
     * @param pageable pagination information
     * @return a page of orders with total amount less than the specified value
     */
    Page<Order> findByTotalAmountLessThan(BigDecimal amount, Pageable pageable);

    /**
     * Finds orders within a total amount range.
     * 
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @param pageable pagination information
     * @return a page of orders within the amount range
     */
    Page<Order> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    /**
     * Finds orders by tracking number.
     * 
     * @param trackingNumber the tracking number
     * @return an optional containing the order if found
     */
    Optional<Order> findByTrackingNumber(String trackingNumber);

    /**
     * Finds orders that can be cancelled.
     * 
     * @param pageable pagination information
     * @return a page of orders that can be cancelled
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED')")
    Page<Order> findCancellableOrders(Pageable pageable);

    /**
     * Finds orders that have been shipped.
     * 
     * @param pageable pagination information
     * @return a page of orders that have been shipped
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('SHIPPED', 'DELIVERED')")
    Page<Order> findShippedOrders(Pageable pageable);

    /**
     * Finds completed orders.
     * 
     * @param pageable pagination information
     * @return a page of completed orders
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED'")
    Page<Order> findCompletedOrders(Pageable pageable);

    /**
     * Finds orders with the highest total amounts.
     * 
     * @param pageable pagination information
     * @return a page of orders ordered by total amount (descending)
     */
    @Query("SELECT o FROM Order o ORDER BY o.totalAmount DESC")
    Page<Order> findOrdersOrderedByTotalAmount(Pageable pageable);

    /**
     * Finds orders with the most items.
     * 
     * @param pageable pagination information
     * @return a page of orders ordered by item count (descending)
     */
    @Query("SELECT o FROM Order o ORDER BY SIZE(o.orderItems) DESC")
    Page<Order> findOrdersOrderedByItemCount(Pageable pageable);

    /**
     * Counts orders by status.
     * 
     * @param status the order status
     * @return the number of orders with the specified status
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Counts orders by user ID.
     * 
     * @param userId the user ID
     * @return the number of orders for the specified user
     */
    long countByUserId(Long userId);

    /**
     * Calculates the total revenue from completed orders.
     * 
     * @return the total revenue
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    Optional<BigDecimal> calculateTotalRevenue();

    /**
     * Calculates the average order value.
     * 
     * @return the average order value
     */
    @Query("SELECT AVG(o.totalAmount) FROM Order o")
    Optional<BigDecimal> calculateAverageOrderValue();

    /**
     * Finds orders created today.
     * 
     * @param pageable pagination information
     * @return a page of orders created today
     */
    @Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE")
    Page<Order> findOrdersCreatedToday(Pageable pageable);

    /**
     * Finds orders created this week.
     * 
     * @param pageable pagination information
     * @return a page of orders created this week
     */
    @Query("SELECT o FROM Order o WHERE YEARWEEK(o.createdAt) = YEARWEEK(CURRENT_DATE)")
    Page<Order> findOrdersCreatedThisWeek(Pageable pageable);

    /**
     * Finds orders created this month.
     * 
     * @param pageable pagination information
     * @return a page of orders created this month
     */
    @Query("SELECT o FROM Order o WHERE YEAR(o.createdAt) = YEAR(CURRENT_DATE) AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    Page<Order> findOrdersCreatedThisMonth(Pageable pageable);

    /**
     * Finds orders by shipping address city.
     * 
     * @param city the shipping address city
     * @param pageable pagination information
     * @return a page of orders shipped to the specified city
     */
    @Query("SELECT o FROM Order o JOIN o.shippingAddress a WHERE a.city = :city")
    Page<Order> findByShippingAddressCity(@Param("city") String city, Pageable pageable);

    /**
     * Finds orders by shipping address country.
     * 
     * @param country the shipping address country
     * @param pageable pagination information
     * @return a page of orders shipped to the specified country
     */
    @Query("SELECT o FROM Order o JOIN o.shippingAddress a WHERE a.country = :country")
    Page<Order> findByShippingAddressCountry(@Param("country") String country, Pageable pageable);
} 