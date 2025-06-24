# Class Hierarchy and Interfaces Design

## 1. Domain Layer (Core Business Logic)

### User Domain

```java
// Core Domain Entity
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private Email email;
    
    @Embedded
    private Password password;
    
    @Embedded
    private UserProfile profile;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    @Embedded
    private AuditInfo auditInfo;
    
    // Business methods
    public boolean canPlaceOrder() { /* implementation */ }
    public boolean isAdmin() { /* implementation */ }
    public void updateProfile(UserProfile newProfile) { /* implementation */ }
}

// Value Objects
@Embeddable
public class Email {
    private String value;
    
    public Email(String value) {
        validateEmail(value);
        this.value = value;
    }
    
    private void validateEmail(String email) {
        // Email validation logic
    }
}

@Embeddable
public class Password {
    private String hashedValue;
    
    public Password(String plainPassword) {
        this.hashedValue = hashPassword(plainPassword);
    }
    
    public boolean matches(String plainPassword) {
        return verifyPassword(plainPassword, hashedValue);
    }
}

@Embeddable
public class UserProfile {
    private String firstName;
    private String lastName;
    private String phone;
    private Address address;
}

// Domain Service Interface
public interface UserDomainService {
    User createUser(CreateUserCommand command);
    void validateUserRegistration(CreateUserCommand command);
    boolean isEmailUnique(Email email);
}

// Domain Service Implementation
@Service
@Transactional
public class UserDomainServiceImpl implements UserDomainService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User createUser(CreateUserCommand command) {
        validateUserRegistration(command);
        
        User user = User.builder()
            .email(new Email(command.getEmail()))
            .password(new Password(command.getPassword()))
            .profile(command.getProfile())
            .role(UserRole.CUSTOMER)
            .build();
            
        return userRepository.save(user);
    }
}
```

### Plant Domain

```java
// Core Domain Entity
@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private PlantId id;
    
    @Embedded
    private PlantDetails details;
    
    @Embedded
    private Pricing pricing;
    
    @Embedded
    private Inventory inventory;
    
    @ElementCollection
    private Set<PlantImage> images;
    
    @Embedded
    private AuditInfo auditInfo;
    
    // Business methods
    public boolean isAvailable() { /* implementation */ }
    public void updateStock(int quantity) { /* implementation */ }
    public Money calculateDiscountedPrice() { /* implementation */ }
    public void addImage(PlantImage image) { /* implementation */ }
}

// Value Objects
@Embeddable
public class PlantDetails {
    private String name;
    private String scientificName;
    private String description;
    private String category;
    private CareLevel careLevel;
    private LightRequirements lightRequirements;
    private WaterRequirements waterRequirements;
}

@Embeddable
public class Pricing {
    private Money price;
    private Money originalPrice;
    private Set<Offer> activeOffers;
    
    public Money getCurrentPrice() {
        return activeOffers.stream()
            .filter(Offer::isActive)
            .map(offer -> offer.applyDiscount(price))
            .min(Money::compareTo)
            .orElse(price);
    }
}

@Embeddable
public class Inventory {
    private int stockQuantity;
    private int reservedQuantity;
    
    public boolean hasAvailableStock(int requestedQuantity) {
        return (stockQuantity - reservedQuantity) >= requestedQuantity;
    }
    
    public void reserveStock(int quantity) {
        if (!hasAvailableStock(quantity)) {
            throw new InsufficientStockException("Insufficient stock available");
        }
        reservedQuantity += quantity;
    }
}

// Domain Service Interface
public interface PlantDomainService {
    Plant createPlant(CreatePlantCommand command);
    Plant updatePlant(PlantId id, UpdatePlantCommand command);
    void updateStock(PlantId id, int quantity);
    void applyOffer(PlantId id, CreateOfferCommand command);
}

// Domain Service Implementation
@Service
@Transactional
public class PlantDomainServiceImpl implements PlantDomainService {
    private final PlantRepository plantRepository;
    private final PlantValidator plantValidator;
    
    @Override
    public Plant createPlant(CreatePlantCommand command) {
        plantValidator.validateCreatePlant(command);
        
        Plant plant = Plant.builder()
            .details(command.getDetails())
            .pricing(command.getPricing())
            .inventory(command.getInventory())
            .build();
            
        return plantRepository.save(plant);
    }
}
```

### Order Domain

```java
// Core Domain Entity
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private OrderId id;
    
    @Embedded
    private OrderNumber orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    @Embedded
    private OrderStatus status;
    
    @Embedded
    private PaymentInfo paymentInfo;
    
    @Embedded
    private ShippingInfo shippingInfo;
    
    @Embedded
    private AuditInfo auditInfo;
    
    // Business methods
    public void addItem(OrderItem item) { /* implementation */ }
    public void removeItem(OrderItemId itemId) { /* implementation */ }
    public Money calculateTotal() { /* implementation */ }
    public void processPayment(PaymentMethod method) { /* implementation */ }
    public void updateStatus(OrderStatus newStatus) { /* implementation */ }
    public boolean canBeCancelled() { /* implementation */ }
}

// Value Objects
@Embeddable
public class OrderNumber {
    private String value;
    
    public OrderNumber() {
        this.value = generateOrderNumber();
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + RandomStringUtils.randomAlphanumeric(6);
    }
}

@Embeddable
public class OrderStatus {
    private Status currentStatus;
    private LocalDateTime lastUpdated;
    private String notes;
    
    public void transitionTo(Status newStatus, String notes) {
        validateTransition(currentStatus, newStatus);
        this.currentStatus = newStatus;
        this.lastUpdated = LocalDateTime.now();
        this.notes = notes;
    }
    
    private void validateTransition(Status current, Status newStatus) {
        // State transition validation logic
    }
}

// Domain Service Interface
public interface OrderDomainService {
    Order createOrder(CreateOrderCommand command);
    Order updateOrderStatus(OrderId id, UpdateOrderStatusCommand command);
    void processPayment(OrderId id, ProcessPaymentCommand command);
    List<Order> getUserOrders(UserId userId);
}

// Domain Service Implementation
@Service
@Transactional
public class OrderDomainServiceImpl implements OrderDomainService {
    private final OrderRepository orderRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final OrderValidator orderValidator;
    private final PaymentProcessor paymentProcessor;
    
    @Override
    public Order createOrder(CreateOrderCommand command) {
        orderValidator.validateCreateOrder(command);
        
        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
            
        Order order = Order.builder()
            .user(user)
            .shippingInfo(command.getShippingInfo())
            .build();
            
        // Add items and validate stock
        command.getItems().forEach(itemCommand -> {
            Plant plant = plantRepository.findById(itemCommand.getPlantId())
                .orElseThrow(() -> new PlantNotFoundException("Plant not found"));
                
            if (!plant.hasAvailableStock(itemCommand.getQuantity())) {
                throw new InsufficientStockException("Insufficient stock for plant: " + plant.getId());
            }
            
            OrderItem item = OrderItem.builder()
                .plant(plant)
                .quantity(itemCommand.getQuantity())
                .unitPrice(plant.getCurrentPrice())
                .build();
                
            order.addItem(item);
            plant.updateStock(-itemCommand.getQuantity());
        });
        
        return orderRepository.save(order);
    }
}
```

## 2. Application Layer (Use Cases)

### User Application Service

```java
// Application Service Interface
public interface UserApplicationService {
    UserDto registerUser(RegisterUserRequest request);
    LoginResponse loginUser(LoginRequest request);
    UserDto getUserProfile(UserId userId);
    UserDto updateUserProfile(UserId userId, UpdateUserProfileRequest request);
    void changePassword(UserId userId, ChangePasswordRequest request);
}

// Application Service Implementation
@Service
@Transactional
public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EventPublisher eventPublisher;
    
    @Override
    public UserDto registerUser(RegisterUserRequest request) {
        // Validate request
        validateRegistrationRequest(request);
        
        // Create user command
        CreateUserCommand command = CreateUserCommand.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .profile(request.getProfile())
            .build();
            
        // Create user using domain service
        User user = userDomainService.createUser(command);
        
        // Publish event
        eventPublisher.publish(new UserRegisteredEvent(user.getId(), user.getEmail()));
        
        // Return DTO
        return userMapper.toDto(user);
    }
    
    @Override
    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(new Email(request.getEmail()))
            .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
            
        if (!user.getPassword().matches(request.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        if (!user.isActive()) {
            throw new UserAccountLockedException("User account is locked");
        }
        
        String token = jwtTokenProvider.generateToken(user);
        
        return LoginResponse.builder()
            .token(token)
            .user(userMapper.toDto(user))
            .build();
    }
}
```

### Plant Application Service

```java
// Application Service Interface
public interface PlantApplicationService {
    PlantDto createPlant(CreatePlantRequest request);
    PlantDto updatePlant(PlantId id, UpdatePlantRequest request);
    PlantDto getPlant(PlantId id);
    Page<PlantDto> searchPlants(PlantSearchRequest request);
    void deletePlant(PlantId id);
    OfferDto createOffer(PlantId id, CreateOfferRequest request);
    void updateOffer(OfferId id, UpdateOfferRequest request);
}

// Application Service Implementation
@Service
@Transactional
public class PlantApplicationServiceImpl implements PlantApplicationService {
    private final PlantDomainService plantDomainService;
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;
    private final PlantSearchService plantSearchService;
    private final EventPublisher eventPublisher;
    
    @Override
    public PlantDto createPlant(CreatePlantRequest request) {
        CreatePlantCommand command = CreatePlantCommand.builder()
            .details(request.getDetails())
            .pricing(request.getPricing())
            .inventory(request.getInventory())
            .build();
            
        Plant plant = plantDomainService.createPlant(command);
        
        eventPublisher.publish(new PlantCreatedEvent(plant.getId(), plant.getDetails().getName()));
        
        return plantMapper.toDto(plant);
    }
    
    @Override
    public Page<PlantDto> searchPlants(PlantSearchRequest request) {
        PlantSearchCriteria criteria = PlantSearchCriteria.builder()
            .category(request.getCategory())
            .searchTerm(request.getSearchTerm())
            .priceRange(request.getPriceRange())
            .careLevel(request.getCareLevel())
            .build();
            
        Page<Plant> plants = plantSearchService.search(criteria, request.getPageable());
        
        return plants.map(plantMapper::toDto);
    }
}
```

### Order Application Service

```java
// Application Service Interface
public interface OrderApplicationService {
    OrderDto createOrder(CreateOrderRequest request);
    OrderDto getOrder(OrderId id);
    Page<OrderDto> getUserOrders(UserId userId, Pageable pageable);
    OrderDto updateOrderStatus(OrderId id, UpdateOrderStatusRequest request);
    void processPayment(OrderId id, ProcessPaymentRequest request);
    void cancelOrder(OrderId id);
}

// Application Service Implementation
@Service
@Transactional
public class OrderApplicationServiceImpl implements OrderApplicationService {
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;
    private final EventPublisher eventPublisher;
    
    @Override
    public OrderDto createOrder(CreateOrderRequest request) {
        CreateOrderCommand command = CreateOrderCommand.builder()
            .userId(request.getUserId())
            .items(request.getItems())
            .shippingInfo(request.getShippingInfo())
            .build();
            
        Order order = orderDomainService.createOrder(command);
        
        eventPublisher.publish(new OrderCreatedEvent(order.getId(), order.getUser().getId()));
        
        return orderMapper.toDto(order);
    }
    
    @Override
    public OrderDto updateOrderStatus(OrderId id, UpdateOrderStatusRequest request) {
        UpdateOrderStatusCommand command = UpdateOrderStatusCommand.builder()
            .orderId(id)
            .newStatus(request.getStatus())
            .notes(request.getNotes())
            .build();
            
        Order order = orderDomainService.updateOrderStatus(command);
        
        eventPublisher.publish(new OrderStatusUpdatedEvent(order.getId(), order.getStatus()));
        
        return orderMapper.toDto(order);
    }
}
```

## 3. Infrastructure Layer

### Repository Interfaces

```java
// Repository Interfaces
public interface UserRepository extends JpaRepository<User, UserId> {
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    List<User> findByRole(UserRole role);
}

public interface PlantRepository extends JpaRepository<Plant, PlantId> {
    List<Plant> findByCategory(String category);
    List<Plant> findByCareLevel(CareLevel careLevel);
    List<Plant> findByIsActiveTrue();
    @Query("SELECT p FROM Plant p WHERE p.details.name LIKE %:searchTerm% OR p.details.description LIKE %:searchTerm%")
    List<Plant> searchPlants(@Param("searchTerm") String searchTerm);
}

public interface OrderRepository extends JpaRepository<Order, OrderId> {
    List<Order> findByUserId(UserId userId);
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findByOrderNumber(OrderNumber orderNumber);
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.auditInfo.createdAt DESC")
    Page<Order> findUserOrders(@Param("userId") UserId userId, Pageable pageable);
}
```

### External Service Interfaces

```java
// Payment Service Interface
public interface PaymentService {
    PaymentResult processPayment(PaymentRequest request);
    PaymentStatus checkPaymentStatus(String paymentId);
    void refundPayment(String paymentId, Money amount);
}

// Email Service Interface
public interface EmailService {
    void sendWelcomeEmail(Email email, String userName);
    void sendOrderConfirmation(Email email, OrderDto order);
    void sendPasswordResetEmail(Email email, String resetToken);
}

// File Storage Service Interface
public interface FileStorageService {
    String uploadImage(MultipartFile file);
    void deleteImage(String imageUrl);
    String getImageUrl(String imageId);
}
```

## 4. Presentation Layer (Controllers)

### REST Controllers

```java
// User Controller
@RestController
@RequestMapping("/api/auth")
@Validated
public class UserController {
    private final UserApplicationService userApplicationService;
    private final RequestValidator requestValidator;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        requestValidator.validate(request);
        UserDto user = userApplicationService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "User registered successfully"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userApplicationService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(@AuthenticationPrincipal UserId userId) {
        UserDto user = userApplicationService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}

// Plant Controller
@RestController
@RequestMapping("/api/plants")
@Validated
public class PlantController {
    private final PlantApplicationService plantApplicationService;
    private final RequestValidator requestValidator;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlantDto>>> searchPlants(PlantSearchRequest request) {
        Page<PlantDto> plants = plantApplicationService.searchPlants(request);
        return ResponseEntity.ok(ApiResponse.success(plants));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantDto>> getPlant(@PathVariable PlantId id) {
        PlantDto plant = plantApplicationService.getPlant(id);
        return ResponseEntity.ok(ApiResponse.success(plant));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlantDto>> createPlant(@Valid @RequestBody CreatePlantRequest request) {
        PlantDto plant = plantApplicationService.createPlant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(plant, "Plant created successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlantDto>> updatePlant(
            @PathVariable PlantId id, 
            @Valid @RequestBody UpdatePlantRequest request) {
        PlantDto plant = plantApplicationService.updatePlant(id, request);
        return ResponseEntity.ok(ApiResponse.success(plant, "Plant updated successfully"));
    }
}

// Order Controller
@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    private final OrderApplicationService orderApplicationService;
    private final RequestValidator requestValidator;
    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = orderApplicationService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(order, "Order created successfully"));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(@PathVariable OrderId id) {
        OrderDto order = orderApplicationService.getOrder(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getUserOrders(
            @AuthenticationPrincipal UserId userId,
            @PageableDefault Pageable pageable) {
        Page<OrderDto> orders = orderApplicationService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable OrderId id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderDto order = orderApplicationService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"));
    }
}
```

This class hierarchy and interface design follows SOLID principles, uses appropriate design patterns, and provides a clean, maintainable, and testable architecture for the Plant E-commerce Order Management system. 