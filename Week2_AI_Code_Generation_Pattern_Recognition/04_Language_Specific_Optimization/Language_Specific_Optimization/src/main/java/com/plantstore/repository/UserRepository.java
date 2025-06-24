package com.plantstore.repository;

import com.plantstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * <p>This interface provides data access methods for User entities
 * including custom queries for common business operations.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return an optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     * 
     * @param email the email
     * @return an optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds users by role.
     * 
     * @param role the user role
     * @param pageable pagination information
     * @return a page of users with the specified role
     */
    Page<User> findByRole(User.Role role, Pageable pageable);

    /**
     * Finds active users.
     * 
     * @param isActive whether the user is active
     * @param pageable pagination information
     * @return a page of active/inactive users
     */
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Finds users by first name containing the specified text (case-insensitive).
     * 
     * @param firstName the first name to search for
     * @param pageable pagination information
     * @return a page of users matching the first name
     */
    Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    /**
     * Finds users by last name containing the specified text (case-insensitive).
     * 
     * @param lastName the last name to search for
     * @param pageable pagination information
     * @return a page of users matching the last name
     */
    Page<User> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    /**
     * Finds users by first name or last name containing the specified text (case-insensitive).
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of users matching the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findByFirstNameOrLastNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, 
                                                            Pageable pageable);

    /**
     * Finds users by username or email containing the specified text (case-insensitive).
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of users matching the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findByUsernameOrEmailContainingIgnoreCase(@Param("searchTerm") String searchTerm, 
                                                        Pageable pageable);

    /**
     * Checks if a user exists by username.
     * 
     * @param username the username
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email.
     * 
     * @param email the email
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds users with the most orders.
     * 
     * @param pageable pagination information
     * @return a page of users ordered by number of orders (descending)
     */
    @Query("SELECT u FROM User u ORDER BY SIZE(u.orders) DESC")
    Page<User> findUsersOrderedByOrderCount(Pageable pageable);

    /**
     * Finds users who have placed orders.
     * 
     * @param pageable pagination information
     * @return a page of users who have at least one order
     */
    @Query("SELECT u FROM User u WHERE SIZE(u.orders) > 0")
    Page<User> findUsersWithOrders(Pageable pageable);

    /**
     * Finds users who have not placed any orders.
     * 
     * @param pageable pagination information
     * @return a page of users who have no orders
     */
    @Query("SELECT u FROM User u WHERE SIZE(u.orders) = 0")
    Page<User> findUsersWithoutOrders(Pageable pageable);

    /**
     * Counts the number of orders for a user.
     * 
     * @param userId the user ID
     * @return the number of orders for the user
     */
    @Query("SELECT SIZE(u.orders) FROM User u WHERE u.id = :userId")
    int countOrdersForUser(@Param("userId") Long userId);

    /**
     * Finds users by phone number.
     * 
     * @param phoneNumber the phone number
     * @return an optional containing the user if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Finds users created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return a page of users created within the date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Page<User> findUsersCreatedBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate,
                                      Pageable pageable);

    /**
     * Finds all admin users.
     * 
     * @return a list of all admin users
     */
    List<User> findByRole(User.Role role);

    /**
     * Finds users with a minimum number of orders.
     * 
     * @param minOrderCount the minimum number of orders
     * @param pageable pagination information
     * @return a page of users with at least the specified number of orders
     */
    @Query("SELECT u FROM User u WHERE SIZE(u.orders) >= :minOrderCount")
    Page<User> findUsersWithMinimumOrderCount(@Param("minOrderCount") int minOrderCount, 
                                             Pageable pageable);
} 