# Method Signatures with Documentation

## 1. Domain Layer Method Signatures

### User Domain Service

```java
/**
 * Domain service for user-related business operations.
 * Follows Single Responsibility Principle by handling only user domain logic.
 */
public interface UserDomainService {
    
    /**
     * Creates a new user with validated business rules.
     * 
     * @param command The command containing user creation data
     * @return The created user entity
     * @throws UserRegistrationException if registration validation fails
     * @throws EmailAlreadyExistsException if email is already registered
     */
    User createUser(CreateUserCommand command);
    
    /**
     * Validates user registration data against business rules.
     * 
     * @param command The command to validate
     * @throws UserRegistrationException if validation fails
     */
    void validateUserRegistration(CreateUserCommand command);
    
    /**
     * Checks if an email address is unique in the system.
     * 
     * @param email The email to check
     * @return true if email is unique, false otherwise
     */
    boolean isEmailUnique(Email email);
    
    /**
     * Updates user profile with validation.
     * 
     * @param userId The user ID
     * @param profile The new profile data
     * @return The updated user
     * @throws UserNotFoundException if user doesn't exist
     */
    User updateUserProfile(UserId userId, UserProfile profile);
    
    /**
     * Changes user password with security validation.
     * 
     * @param userId The user ID
     * @param currentPassword The current password
     * @param newPassword The new password
     * @throws AuthenticationException if current password is incorrect
     * @throws PasswordValidationException if new password doesn't meet requirements
     */
    void changePassword(UserId userId, String currentPassword, String newPassword);
}
```

### Plant Domain Service

```java
/**
 * Domain service for plant-related business operations.
 * Handles plant creation, updates, and inventory management.
 */
public interface PlantDomainService {
    
    /**
     * Creates a new plant with validated business rules.
     * 
     * @param command The command containing plant creation data
     * @return The created plant entity
     * @throws PlantValidationException if plant data is invalid
     * @throws DuplicatePlantException if plant with same name already exists
     */
    Plant createPlant(CreatePlantCommand command);
    
    /**
     * Updates an existing plant with validation.
     * 
     * @param id The plant ID
     * @param command The update command
     * @return The updated plant
     * @throws PlantNotFoundException if plant doesn't exist
     * @throws PlantValidationException if update data is invalid
     */
    Plant updatePlant(PlantId id, UpdatePlantCommand command);
    
    /**
     * Updates plant stock quantity with business validation.
     * 
     * @param id The plant ID
     * @param quantity The quantity to add (positive) or subtract (negative)
     * @throws PlantNotFoundException if plant doesn't exist
     * @throws InsufficientStockException if trying to subtract more than available
     */
    void updateStock(PlantId id, int quantity);
    
    /**
     * Applies an offer to a plant with validation.
     * 
     * @param id The plant ID
     * @param command The offer creation command
     * @throws PlantNotFoundException if plant doesn't exist
     * @throws OfferValidationException if offer data is invalid
     */
    void applyOffer(PlantId id, CreateOfferCommand command);
    
    /**
     * Removes a plant from the catalog (soft delete).
     * 
     * @param id The plant ID
     * @throws PlantNotFoundException if plant doesn't exist
     */
    void deactivatePlant(PlantId id);
    
    /**
     * Validates if plant can be ordered.
     * 
     * @param id The plant ID
     * @param quantity The requested quantity
     * @return true if plant can be ordered, false otherwise
     * @throws PlantNotFoundException if plant doesn't exist
     */
    boolean canOrderPlant(PlantId id, int quantity);
}
```

### Order Domain Service

```java
/**
 * Domain service for order-related business operations.
 * Handles order creation, status updates, and payment processing.
 */
public interface OrderDomainService {
    
    /**
     * Creates a new order with comprehensive validation.
     * 
     * @param command The command containing order creation data
     * @return The created order entity
     * @throws OrderValidationException if order data is invalid
     * @throws InsufficientStockException if any plant has insufficient stock
     * @throws UserNotFoundException if user doesn't exist
     */
    Order createOrder(CreateOrderCommand command);
    
    /**
     * Updates order status with state transition validation.
     * 
     * @param id The order ID
     * @param command The status update command
     * @return The updated order
     * @throws OrderNotFoundException if order doesn't exist
     * @throws InvalidStatusTransitionException if status transition is not allowed
     */
    Order updateOrderStatus(OrderId id, UpdateOrderStatusCommand command);
    
    /**
     * Processes payment for an order.
     * 
     * @param id The order ID
     * @param command The payment processing command
     * @return Payment result
     * @throws OrderNotFoundException if order doesn't exist
     * @throws PaymentProcessingException if payment fails
     * @throws InvalidPaymentMethodException if payment method is not supported
     */
    PaymentResult processPayment(OrderId id, ProcessPaymentCommand command);
    
    /**
     * Retrieves orders for a specific user.
     * 
     * @param userId The user ID
     * @return List of user's orders
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Order> getUserOrders(UserId userId);
    
    /**
     * Cancels an order if it's in a cancellable state.
     * 
     * @param id The order ID
     * @param reason The cancellation reason
     * @return The cancelled order
     * @throws OrderNotFoundException if order doesn't exist
     * @throws OrderCannotBeCancelledException if order is not in cancellable state
     */
    Order cancelOrder(OrderId id, String reason);
    
    /**
     * Calculates order total with all applicable discounts.
     * 
     * @param id The order ID
     * @return The calculated total amount
     * @throws OrderNotFoundException if order doesn't exist
     */
    Money calculateOrderTotal(OrderId id);
}
```

## 2. Application Layer Method Signatures

### User Application Service

```java
/**
 * Application service for user use cases.
 * Orchestrates user-related operations and coordinates between domain services.
 */
public interface UserApplicationService {
    
    /**
     * Registers a new user in the system.
     * 
     * @param request The registration request containing user data
     * @return User DTO with registration result
     * @throws UserRegistrationException if registration fails
     * @throws EmailAlreadyExistsException if email is already registered
     */
    UserDto registerUser(RegisterUserRequest request);
    
    /**
     * Authenticates user and generates JWT token.
     * 
     * @param request The login request containing credentials
     * @return Login response with token and user data
     * @throws AuthenticationException if credentials are invalid
     * @throws UserAccountLockedException if account is locked
     */
    LoginResponse loginUser(LoginRequest request);
    
    /**
     * Retrieves user profile information.
     * 
     * @param userId The user ID
     * @return User DTO with profile data
     * @throws UserNotFoundException if user doesn't exist
     */
    UserDto getUserProfile(UserId userId);
    
    /**
     * Updates user profile information.
     * 
     * @param userId The user ID
     * @param request The profile update request
     * @return Updated user DTO
     * @throws UserNotFoundException if user doesn't exist
     * @throws ProfileUpdateException if update fails
     */
    UserDto updateUserProfile(UserId userId, UpdateUserProfileRequest request);
    
    /**
     * Changes user password with security validation.
     * 
     * @param userId The user ID
     * @param request The password change request
     * @throws UserNotFoundException if user doesn't exist
     * @throws AuthenticationException if current password is incorrect
     * @throws PasswordValidationException if new password doesn't meet requirements
     */
    void changePassword(UserId userId, ChangePasswordRequest request);
    
    /**
     * Initiates password reset process.
     * 
     * @param request The password reset request
     * @throws UserNotFoundException if email doesn't exist
     */
    void initiatePasswordReset(PasswordResetRequest request);
    
    /**
     * Completes password reset with token validation.
     * 
     * @param request The password reset completion request
     * @throws InvalidResetTokenException if token is invalid or expired
     * @throws PasswordValidationException if new password doesn't meet requirements
     */
    void completePasswordReset(PasswordResetCompletionRequest request);
}
```

### Plant Application Service

```java
/**
 * Application service for plant use cases.
 * Orchestrates plant-related operations and coordinates between domain services.
 */
public interface PlantApplicationService {
    
    /**
     * Creates a new plant in the catalog.
     * 
     * @param request The plant creation request
     * @return Plant DTO with creation result
     * @throws PlantValidationException if plant data is invalid
     * @throws DuplicatePlantException if plant with same name already exists
     */
    PlantDto createPlant(CreatePlantRequest request);
    
    /**
     * Updates an existing plant in the catalog.
     * 
     * @param id The plant ID
     * @param request The plant update request
     * @return Updated plant DTO
     * @throws PlantNotFoundException if plant doesn't exist
     * @throws PlantValidationException if update data is invalid
     */
    PlantDto updatePlant(PlantId id, UpdatePlantRequest request);
    
    /**
     * Retrieves a specific plant by ID.
     * 
     * @param id The plant ID
     * @return Plant DTO
     * @throws PlantNotFoundException if plant doesn't exist
     */
    PlantDto getPlant(PlantId id);
    
    /**
     * Searches plants based on various criteria.
     * 
     * @param request The search request with criteria
     * @return Page of plant DTOs matching criteria
     */
    Page<PlantDto> searchPlants(PlantSearchRequest request);
    
    /**
     * Deletes a plant from the catalog (soft delete).
     * 
     * @param id The plant ID
     * @throws PlantNotFoundException if plant doesn't exist
     */
    void deletePlant(PlantId id);
    
    /**
     * Creates an offer for a specific plant.
     * 
     * @param id The plant ID
     * @param request The offer creation request
     * @return Offer DTO
     * @throws PlantNotFoundException if plant doesn't exist
     * @throws OfferValidationException if offer data is invalid
     */
    OfferDto createOffer(PlantId id, CreateOfferRequest request);
    
    /**
     * Updates an existing offer.
     * 
     * @param id The offer ID
     * @param request The offer update request
     * @return Updated offer DTO
     * @throws OfferNotFoundException if offer doesn't exist
     * @throws OfferValidationException if update data is invalid
     */
    OfferDto updateOffer(OfferId id, UpdateOfferRequest request);
    
    /**
     * Uploads images for a plant.
     * 
     * @param id The plant ID
     * @param files The image files to upload
     * @return List of uploaded image URLs
     * @throws PlantNotFoundException if plant doesn't exist
     * @throws ImageUploadException if upload fails
     */
    List<String> uploadPlantImages(PlantId id, List<MultipartFile> files);
    
    /**
     * Gets plant categories for filtering.
     * 
     * @return List of available categories
     */
    List<String> getPlantCategories();
}
```

### Order Application Service

```java
/**
 * Application service for order use cases.
 * Orchestrates order-related operations and coordinates between domain services.
 */
public interface OrderApplicationService {
    
    /**
     * Creates a new order for a user.
     * 
     * @param request The order creation request
     * @return Order DTO with creation result
     * @throws OrderValidationException if order data is invalid
     * @throws InsufficientStockException if any plant has insufficient stock
     * @throws UserNotFoundException if user doesn't exist
     */
    OrderDto createOrder(CreateOrderRequest request);
    
    /**
     * Retrieves a specific order by ID.
     * 
     * @param id The order ID
     * @return Order DTO
     * @throws OrderNotFoundException if order doesn't exist
     */
    OrderDto getOrder(OrderId id);
    
    /**
     * Retrieves orders for a specific user with pagination.
     * 
     * @param userId The user ID
     * @param pageable The pagination parameters
     * @return Page of order DTOs
     * @throws UserNotFoundException if user doesn't exist
     */
    Page<OrderDto> getUserOrders(UserId userId, Pageable pageable);
    
    /**
     * Updates order status (admin only).
     * 
     * @param id The order ID
     * @param request The status update request
     * @return Updated order DTO
     * @throws OrderNotFoundException if order doesn't exist
     * @throws InvalidStatusTransitionException if status transition is not allowed
     */
    OrderDto updateOrderStatus(OrderId id, UpdateOrderStatusRequest request);
    
    /**
     * Processes payment for an order.
     * 
     * @param id The order ID
     * @param request The payment processing request
     * @return Payment result
     * @throws OrderNotFoundException if order doesn't exist
     * @throws PaymentProcessingException if payment fails
     */
    PaymentResult processPayment(OrderId id, ProcessPaymentRequest request);
    
    /**
     * Cancels an order if it's in a cancellable state.
     * 
     * @param id The order ID
     * @return Cancelled order DTO
     * @throws OrderNotFoundException if order doesn't exist
     * @throws OrderCannotBeCancelledException if order is not in cancellable state
     */
    OrderDto cancelOrder(OrderId id);
    
    /**
     * Gets order tracking information.
     * 
     * @param id The order ID
     * @return Order tracking DTO
     * @throws OrderNotFoundException if order doesn't exist
     */
    OrderTrackingDto getOrderTracking(OrderId id);
    
    /**
     * Gets order statistics for admin dashboard.
     * 
     * @param request The statistics request with date range
     * @return Order statistics DTO
     */
    OrderStatisticsDto getOrderStatistics(OrderStatisticsRequest request);
}
```

## 3. Infrastructure Layer Method Signatures

### Repository Interfaces

```java
/**
 * Repository interface for User entity operations.
 * Extends JpaRepository for basic CRUD operations and adds custom query methods.
 */
public interface UserRepository extends JpaRepository<User, UserId> {
    
    /**
     * Finds user by email address.
     * 
     * @param email The email to search for
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Checks if email exists in the system.
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(Email email);
    
    /**
     * Finds users by role.
     * 
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Finds active users.
     * 
     * @return List of active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Searches users by name (first name or last name).
     * 
     * @param name The name to search for
     * @return List of users matching the name
     */
    @Query("SELECT u FROM User u WHERE u.profile.firstName LIKE %:name% OR u.profile.lastName LIKE %:name%")
    List<User> searchByName(@Param("name") String name);
}

/**
 * Repository interface for Plant entity operations.
 */
public interface PlantRepository extends JpaRepository<Plant, PlantId> {
    
    /**
     * Finds plants by category.
     * 
     * @param category The category to filter by
     * @return List of plants in the category
     */
    List<Plant> findByCategory(String category);
    
    /**
     * Finds plants by care level.
     * 
     * @param careLevel The care level to filter by
     * @return List of plants with the care level
     */
    List<Plant> findByCareLevel(CareLevel careLevel);
    
    /**
     * Finds active plants.
     * 
     * @return List of active plants
     */
    List<Plant> findByIsActiveTrue();
    
    /**
     * Searches plants by name or description.
     * 
     * @param searchTerm The search term
     * @return List of plants matching the search term
     */
    @Query("SELECT p FROM Plant p WHERE p.details.name LIKE %:searchTerm% OR p.details.description LIKE %:searchTerm%")
    List<Plant> searchPlants(@Param("searchTerm") String searchTerm);
    
    /**
     * Finds plants with low stock (below threshold).
     * 
     * @param threshold The stock threshold
     * @return List of plants with low stock
     */
    @Query("SELECT p FROM Plant p WHERE p.inventory.stockQuantity <= :threshold")
    List<Plant> findLowStockPlants(@Param("threshold") int threshold);
}

/**
 * Repository interface for Order entity operations.
 */
public interface OrderRepository extends JpaRepository<Order, OrderId> {
    
    /**
     * Finds orders by user ID.
     * 
     * @param userId The user ID
     * @return List of user's orders
     */
    List<Order> findByUserId(UserId userId);
    
    /**
     * Finds orders by status.
     * 
     * @param status The order status
     * @return List of orders with the status
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Finds order by order number.
     * 
     * @param orderNumber The order number
     * @return Optional containing order if found
     */
    Optional<Order> findByOrderNumber(OrderNumber orderNumber);
    
    /**
     * Finds user orders with pagination.
     * 
     * @param userId The user ID
     * @param pageable The pagination parameters
     * @return Page of user's orders
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.auditInfo.createdAt DESC")
    Page<Order> findUserOrders(@Param("userId") UserId userId, Pageable pageable);
    
    /**
     * Finds orders created within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of orders in the date range
     */
    @Query("SELECT o FROM Order o WHERE o.auditInfo.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
```

### External Service Interfaces

```java
/**
 * Service interface for payment processing operations.
 * Abstracts payment gateway integration.
 */
public interface PaymentService {
    
    /**
     * Processes a payment for an order.
     * 
     * @param request The payment request
     * @return Payment result with status and transaction details
     * @throws PaymentProcessingException if payment processing fails
     * @throws InvalidPaymentMethodException if payment method is not supported
     */
    PaymentResult processPayment(PaymentRequest request);
    
    /**
     * Checks the status of a payment.
     * 
     * @param paymentId The payment ID
     * @return Payment status
     * @throws PaymentNotFoundException if payment doesn't exist
     */
    PaymentStatus checkPaymentStatus(String paymentId);
    
    /**
     * Refunds a payment.
     * 
     * @param paymentId The payment ID
     * @param amount The amount to refund
     * @return Refund result
     * @throws PaymentNotFoundException if payment doesn't exist
     * @throws RefundProcessingException if refund processing fails
     */
    RefundResult refundPayment(String paymentId, Money amount);
    
    /**
     * Validates payment method.
     * 
     * @param paymentMethod The payment method to validate
     * @return true if valid, false otherwise
     */
    boolean validatePaymentMethod(PaymentMethod paymentMethod);
}

/**
 * Service interface for email operations.
 * Abstracts email service integration.
 */
public interface EmailService {
    
    /**
     * Sends welcome email to newly registered user.
     * 
     * @param email The recipient email
     * @param userName The user's name
     * @throws EmailSendingException if email sending fails
     */
    void sendWelcomeEmail(Email email, String userName);
    
    /**
     * Sends order confirmation email.
     * 
     * @param email The recipient email
     * @param order The order details
     * @throws EmailSendingException if email sending fails
     */
    void sendOrderConfirmation(Email email, OrderDto order);
    
    /**
     * Sends password reset email.
     * 
     * @param email The recipient email
     * @param resetToken The password reset token
     * @throws EmailSendingException if email sending fails
     */
    void sendPasswordResetEmail(Email email, String resetToken);
    
    /**
     * Sends order status update notification.
     * 
     * @param email The recipient email
     * @param order The order with updated status
     * @throws EmailSendingException if email sending fails
     */
    void sendOrderStatusUpdate(Email email, OrderDto order);
}

/**
 * Service interface for file storage operations.
 * Abstracts file storage service integration.
 */
public interface FileStorageService {
    
    /**
     * Uploads an image file.
     * 
     * @param file The file to upload
     * @return The URL of the uploaded file
     * @throws ImageUploadException if upload fails
     * @throws InvalidFileTypeException if file type is not supported
     */
    String uploadImage(MultipartFile file);
    
    /**
     * Deletes an image file.
     * 
     * @param imageUrl The URL of the image to delete
     * @throws ImageDeletionException if deletion fails
     */
    void deleteImage(String imageUrl);
    
    /**
     * Gets the URL for an image by ID.
     * 
     * @param imageId The image ID
     * @return The image URL
     * @throws ImageNotFoundException if image doesn't exist
     */
    String getImageUrl(String imageId);
    
    /**
     * Validates image file.
     * 
     * @param file The file to validate
     * @return true if valid, false otherwise
     */
    boolean validateImage(MultipartFile file);
}
```

These method signatures provide comprehensive documentation and follow best practices for Java development, including proper exception handling, validation, and clear parameter/return type definitions. 