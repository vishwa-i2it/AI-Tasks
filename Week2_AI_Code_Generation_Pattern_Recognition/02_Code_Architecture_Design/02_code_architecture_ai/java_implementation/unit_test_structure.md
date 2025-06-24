# Unit Test Structure Outline

## 1. Test Configuration and Setup

### Test Configuration

```java
/**
 * Base test configuration for all unit tests.
 * Provides common test utilities and configurations.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class BaseUnitTest {
    
    @Mock
    protected UserRepository userRepository;
    
    @Mock
    protected PlantRepository plantRepository;
    
    @Mock
    protected OrderRepository orderRepository;
    
    @Mock
    protected PasswordEncoder passwordEncoder;
    
    @Mock
    protected JwtTokenProvider jwtTokenProvider;
    
    @Mock
    protected EventPublisher eventPublisher;
    
    @Mock
    protected EmailService emailService;
    
    @Mock
    protected PaymentService paymentService;
    
    @Mock
    protected FileStorageService fileStorageService;
    
    @Mock
    protected UserMapper userMapper;
    
    @Mock
    protected PlantMapper plantMapper;
    
    @Mock
    protected OrderMapper orderMapper;
    
    /**
     * Creates a test user with default values.
     */
    protected User createTestUser() {
        return User.builder()
            .id(new UserId(1L))
            .email(new Email("test@example.com"))
            .password(new Password("hashedPassword"))
            .profile(UserProfile.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .build())
            .role(UserRole.CUSTOMER)
            .isActive(true)
            .build();
    }
    
    /**
     * Creates a test plant with default values.
     */
    protected Plant createTestPlant() {
        return Plant.builder()
            .id(new PlantId(1L))
            .details(PlantDetails.builder()
                .name("Test Plant")
                .description("A test plant")
                .category("Indoor")
                .careLevel(CareLevel.EASY)
                .build())
            .pricing(Pricing.builder()
                .price(new Money(BigDecimal.valueOf(29.99)))
                .build())
            .inventory(Inventory.builder()
                .stockQuantity(10)
                .build())
            .isActive(true)
            .build();
    }
    
    /**
     * Creates a test order with default values.
     */
    protected Order createTestOrder() {
        return Order.builder()
            .id(new OrderId(1L))
            .orderNumber(new OrderNumber())
            .user(createTestUser())
            .status(OrderStatus.builder()
                .currentStatus(Status.PENDING)
                .build())
            .shippingInfo(ShippingInfo.builder()
                .address("123 Test St")
                .city("Test City")
                .zipCode("12345")
                .build())
            .build();
    }
    
    /**
     * Verifies that an exception was thrown with the expected type and message.
     */
    protected <T extends Throwable> T assertThrowsWithMessage(Class<T> exceptionClass, 
                                                             String expectedMessage, 
                                                             Executable executable) {
        T exception = assertThrows(exceptionClass, executable);
        assertThat(exception.getMessage()).contains(expectedMessage);
        return exception;
    }
}
```

### Test Data Builders

```java
/**
 * Test data builders for creating test objects with fluent API.
 */
public class TestDataBuilder {
    
    public static class UserBuilder {
        private UserId id = new UserId(1L);
        private Email email = new Email("test@example.com");
        private Password password = new Password("hashedPassword");
        private UserProfile profile = UserProfile.builder()
            .firstName("John")
            .lastName("Doe")
            .phone("1234567890")
            .build();
        private UserRole role = UserRole.CUSTOMER;
        private boolean isActive = true;
        
        public UserBuilder id(UserId id) {
            this.id = id;
            return this;
        }
        
        public UserBuilder email(Email email) {
            this.email = email;
            return this;
        }
        
        public UserBuilder password(Password password) {
            this.password = password;
            return this;
        }
        
        public UserBuilder profile(UserProfile profile) {
            this.profile = profile;
            return this;
        }
        
        public UserBuilder role(UserRole role) {
            this.role = role;
            return this;
        }
        
        public UserBuilder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }
        
        public User build() {
            return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .profile(profile)
                .role(role)
                .isActive(isActive)
                .build();
        }
    }
    
    public static class PlantBuilder {
        private PlantId id = new PlantId(1L);
        private PlantDetails details = PlantDetails.builder()
            .name("Test Plant")
            .description("A test plant")
            .category("Indoor")
            .careLevel(CareLevel.EASY)
            .build();
        private Pricing pricing = Pricing.builder()
            .price(new Money(BigDecimal.valueOf(29.99)))
            .build();
        private Inventory inventory = Inventory.builder()
            .stockQuantity(10)
            .build();
        private boolean isActive = true;
        
        public PlantBuilder id(PlantId id) {
            this.id = id;
            return this;
        }
        
        public PlantBuilder details(PlantDetails details) {
            this.details = details;
            return this;
        }
        
        public PlantBuilder pricing(Pricing pricing) {
            this.pricing = pricing;
            return this;
        }
        
        public PlantBuilder inventory(Inventory inventory) {
            this.inventory = inventory;
            return this;
        }
        
        public PlantBuilder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }
        
        public Plant build() {
            return Plant.builder()
                .id(id)
                .details(details)
                .pricing(pricing)
                .inventory(inventory)
                .isActive(isActive)
                .build();
        }
    }
    
    public static UserBuilder user() {
        return new UserBuilder();
    }
    
    public static PlantBuilder plant() {
        return new PlantBuilder();
    }
}
```

## 2. Domain Layer Tests

### User Domain Service Tests

```java
/**
 * Unit tests for UserDomainService.
 * Tests business logic and validation rules.
 */
@ExtendWith(MockitoExtension.class)
class UserDomainServiceImplTest extends BaseUnitTest {
    
    @InjectMocks
    private UserDomainServiceImpl userDomainService;
    
    @Mock
    private UserValidator userValidator;
    
    @Test
    @DisplayName("Should create user successfully with valid data")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserCommand command = CreateUserCommand.builder()
            .email("test@example.com")
            .password("password123")
            .profile(UserProfile.builder()
                .firstName("John")
                .lastName("Doe")
                .build())
            .build();
        
        User expectedUser = TestDataBuilder.user()
            .email(new Email("test@example.com"))
            .build();
        
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        
        // When
        User result = userDomainService.createUser(command);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail().getValue()).isEqualTo("test@example.com");
        verify(userValidator).validateCreateUser(command);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        CreateUserCommand command = CreateUserCommand.builder()
            .email("existing@example.com")
            .password("password123")
            .build();
        
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);
        
        // When & Then
        EmailAlreadyExistsException exception = assertThrowsWithMessage(
            EmailAlreadyExistsException.class,
            "existing@example.com",
            () -> userDomainService.createUser(command)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }
    
    @Test
    @DisplayName("Should validate user registration successfully")
    void shouldValidateUserRegistrationSuccessfully() {
        // Given
        CreateUserCommand command = CreateUserCommand.builder()
            .email("test@example.com")
            .password("password123")
            .build();
        
        // When
        userDomainService.validateUserRegistration(command);
        
        // Then
        verify(userValidator).validateCreateUser(command);
    }
    
    @Test
    @DisplayName("Should check email uniqueness correctly")
    void shouldCheckEmailUniquenessCorrectly() {
        // Given
        Email email = new Email("test@example.com");
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // When
        boolean isUnique = userDomainService.isEmailUnique(email);
        
        // Then
        assertThat(isUnique).isTrue();
        verify(userRepository).existsByEmail(email);
    }
}
```

### Plant Domain Service Tests

```java
/**
 * Unit tests for PlantDomainService.
 * Tests plant business logic and validation.
 */
@ExtendWith(MockitoExtension.class)
class PlantDomainServiceImplTest extends BaseUnitTest {
    
    @InjectMocks
    private PlantDomainServiceImpl plantDomainService;
    
    @Mock
    private PlantValidator plantValidator;
    
    @Test
    @DisplayName("Should create plant successfully with valid data")
    void shouldCreatePlantSuccessfully() {
        // Given
        CreatePlantCommand command = CreatePlantCommand.builder()
            .details(PlantDetails.builder()
                .name("Test Plant")
                .description("A test plant")
                .category("Indoor")
                .build())
            .pricing(Pricing.builder()
                .price(new Money(BigDecimal.valueOf(29.99)))
                .build())
            .inventory(Inventory.builder()
                .stockQuantity(10)
                .build())
            .build();
        
        Plant expectedPlant = TestDataBuilder.plant()
            .details(command.getDetails())
            .build();
        
        when(plantRepository.save(any(Plant.class))).thenReturn(expectedPlant);
        
        // When
        Plant result = plantDomainService.createPlant(command);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDetails().getName()).isEqualTo("Test Plant");
        verify(plantValidator).validateCreatePlant(command);
        verify(plantRepository).save(any(Plant.class));
    }
    
    @Test
    @DisplayName("Should update plant stock successfully")
    void shouldUpdatePlantStockSuccessfully() {
        // Given
        PlantId plantId = new PlantId(1L);
        Plant plant = TestDataBuilder.plant()
            .id(plantId)
            .inventory(Inventory.builder().stockQuantity(10).build())
            .build();
        
        when(plantRepository.findById(plantId)).thenReturn(Optional.of(plant));
        when(plantRepository.save(any(Plant.class))).thenReturn(plant);
        
        // When
        plantDomainService.updateStock(plantId, 5);
        
        // Then
        verify(plantRepository).findById(plantId);
        verify(plantRepository).save(any(Plant.class));
        assertThat(plant.getInventory().getStockQuantity()).isEqualTo(15);
    }
    
    @Test
    @DisplayName("Should throw exception when plant not found")
    void shouldThrowExceptionWhenPlantNotFound() {
        // Given
        PlantId plantId = new PlantId(999L);
        when(plantRepository.findById(plantId)).thenReturn(Optional.empty());
        
        // When & Then
        PlantNotFoundException exception = assertThrowsWithMessage(
            PlantNotFoundException.class,
            "999",
            () -> plantDomainService.updateStock(plantId, 5)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("PLANT_NOT_FOUND");
    }
    
    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void shouldThrowExceptionWhenInsufficientStock() {
        // Given
        PlantId plantId = new PlantId(1L);
        Plant plant = TestDataBuilder.plant()
            .id(plantId)
            .inventory(Inventory.builder().stockQuantity(5).build())
            .build();
        
        when(plantRepository.findById(plantId)).thenReturn(Optional.of(plant));
        
        // When & Then
        InsufficientStockException exception = assertThrowsWithMessage(
            InsufficientStockException.class,
            "Insufficient stock",
            () -> plantDomainService.updateStock(plantId, -10)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("INSUFFICIENT_STOCK");
    }
}
```

### Order Domain Service Tests

```java
/**
 * Unit tests for OrderDomainService.
 * Tests order business logic and validation.
 */
@ExtendWith(MockitoExtension.class)
class OrderDomainServiceImplTest extends BaseUnitTest {
    
    @InjectMocks
    private OrderDomainServiceImpl orderDomainService;
    
    @Mock
    private OrderValidator orderValidator;
    
    @Mock
    private PaymentProcessor paymentProcessor;
    
    @Test
    @DisplayName("Should create order successfully with valid data")
    void shouldCreateOrderSuccessfully() {
        // Given
        User user = createTestUser();
        Plant plant = createTestPlant();
        
        CreateOrderCommand command = CreateOrderCommand.builder()
            .userId(user.getId())
            .items(List.of(OrderItemCommand.builder()
                .plantId(plant.getId())
                .quantity(2)
                .build()))
            .shippingInfo(ShippingInfo.builder()
                .address("123 Test St")
                .build())
            .build();
        
        Order expectedOrder = createTestOrder();
        
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(plantRepository.findById(plant.getId())).thenReturn(Optional.of(plant));
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);
        
        // When
        Order result = orderDomainService.createOrder(command);
        
        // Then
        assertThat(result).isNotNull();
        verify(orderValidator).validateCreateOrder(command);
        verify(userRepository).findById(user.getId());
        verify(plantRepository).findById(plant.getId());
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Should update order status successfully")
    void shouldUpdateOrderStatusSuccessfully() {
        // Given
        OrderId orderId = new OrderId(1L);
        Order order = createTestOrder();
        order.setId(orderId);
        
        UpdateOrderStatusCommand command = UpdateOrderStatusCommand.builder()
            .orderId(orderId)
            .newStatus(Status.CONFIRMED)
            .notes("Order confirmed")
            .build();
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        // When
        Order result = orderDomainService.updateOrderStatus(command);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCurrentStatus()).isEqualTo(Status.CONFIRMED);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        OrderId orderId = new OrderId(999L);
        UpdateOrderStatusCommand command = UpdateOrderStatusCommand.builder()
            .orderId(orderId)
            .newStatus(Status.CONFIRMED)
            .build();
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // When & Then
        OrderNotFoundException exception = assertThrowsWithMessage(
            OrderNotFoundException.class,
            "999",
            () -> orderDomainService.updateOrderStatus(command)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("ORDER_NOT_FOUND");
    }
}
```

## 3. Application Layer Tests

### User Application Service Tests

```java
/**
 * Unit tests for UserApplicationService.
 * Tests use case orchestration and coordination.
 */
@ExtendWith(MockitoExtension.class)
class UserApplicationServiceImplTest extends BaseUnitTest {
    
    @InjectMocks
    private UserApplicationServiceImpl userApplicationService;
    
    @Mock
    private UserDomainService userDomainService;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private UserMapper userMapper;
    
    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
            .email("test@example.com")
            .password("password123")
            .firstName("John")
            .lastName("Doe")
            .build();
        
        User user = createTestUser();
        UserDto userDto = UserDto.builder()
            .id(1L)
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .build();
        
        when(userDomainService.createUser(any(CreateUserCommand.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        
        // When
        UserDto result = userApplicationService.registerUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userDomainService).createUser(any(CreateUserCommand.class));
        verify(eventPublisher).publish(any(UserRegisteredEvent.class));
        verify(emailService).sendWelcomeEmail(any(Email.class), anyString());
    }
    
    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("test@example.com")
            .password("password123")
            .build();
        
        User user = createTestUser();
        UserDto userDto = UserDto.builder()
            .id(1L)
            .email("test@example.com")
            .build();
        
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(user.getPassword().matches(anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(user)).thenReturn("jwt-token");
        when(userMapper.toDto(user)).thenReturn(userDto);
        
        // When
        LoginResponse result = userApplicationService.loginUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUser()).isEqualTo(userDto);
        verify(userRepository).findByEmail(any(Email.class));
        verify(jwtTokenProvider).generateToken(user);
    }
    
    @Test
    @DisplayName("Should throw exception when login credentials are invalid")
    void shouldThrowExceptionWhenLoginCredentialsAreInvalid() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("test@example.com")
            .password("wrongpassword")
            .build();
        
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        
        // When & Then
        AuthenticationException exception = assertThrowsWithMessage(
            AuthenticationException.class,
            "Invalid credentials",
            () -> userApplicationService.loginUser(request)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("AUTHENTICATION_ERROR");
    }
}
```

### Plant Application Service Tests

```java
/**
 * Unit tests for PlantApplicationService.
 * Tests plant use case orchestration.
 */
@ExtendWith(MockitoExtension.class)
class PlantApplicationServiceImplTest extends BaseUnitTest {
    
    @InjectMocks
    private PlantApplicationServiceImpl plantApplicationService;
    
    @Mock
    private PlantDomainService plantDomainService;
    
    @Mock
    private PlantSearchService plantSearchService;
    
    @Mock
    private PlantMapper plantMapper;
    
    @Mock
    private FileStorageService fileStorageService;
    
    @Test
    @DisplayName("Should create plant successfully")
    void shouldCreatePlantSuccessfully() {
        // Given
        CreatePlantRequest request = CreatePlantRequest.builder()
            .name("Test Plant")
            .description("A test plant")
            .price(BigDecimal.valueOf(29.99))
            .category("Indoor")
            .stockQuantity(10)
            .build();
        
        Plant plant = createTestPlant();
        PlantDto plantDto = PlantDto.builder()
            .id(1L)
            .name("Test Plant")
            .price(BigDecimal.valueOf(29.99))
            .build();
        
        when(plantDomainService.createPlant(any(CreatePlantCommand.class))).thenReturn(plant);
        when(plantMapper.toDto(plant)).thenReturn(plantDto);
        
        // When
        PlantDto result = plantApplicationService.createPlant(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Plant");
        verify(plantDomainService).createPlant(any(CreatePlantCommand.class));
        verify(eventPublisher).publish(any(PlantCreatedEvent.class));
    }
    
    @Test
    @DisplayName("Should search plants successfully")
    void shouldSearchPlantsSuccessfully() {
        // Given
        PlantSearchRequest request = PlantSearchRequest.builder()
            .searchTerm("plant")
            .category("Indoor")
            .page(0)
            .size(10)
            .build();
        
        Plant plant = createTestPlant();
        PlantDto plantDto = PlantDto.builder()
            .id(1L)
            .name("Test Plant")
            .build();
        
        Page<Plant> plantPage = new PageImpl<>(List.of(plant));
        Page<PlantDto> plantDtoPage = new PageImpl<>(List.of(plantDto));
        
        when(plantSearchService.search(any(PlantSearchCriteria.class), any(Pageable.class)))
            .thenReturn(plantPage);
        when(plantMapper.toDto(plant)).thenReturn(plantDto);
        
        // When
        Page<PlantDto> result = plantApplicationService.searchPlants(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Plant");
        verify(plantSearchService).search(any(PlantSearchCriteria.class), any(Pageable.class));
    }
    
    @Test
    @DisplayName("Should upload plant images successfully")
    void shouldUploadPlantImagesSuccessfully() {
        // Given
        PlantId plantId = new PlantId(1L);
        List<MultipartFile> files = List.of(
            createMockMultipartFile("image1.jpg"),
            createMockMultipartFile("image2.jpg")
        );
        
        when(fileStorageService.uploadImage(any(MultipartFile.class)))
            .thenReturn("https://example.com/image1.jpg")
            .thenReturn("https://example.com/image2.jpg");
        
        // When
        List<String> result = plantApplicationService.uploadPlantImages(plantId, files);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo("https://example.com/image1.jpg");
        assertThat(result.get(1)).isEqualTo("https://example.com/image2.jpg");
        verify(fileStorageService, times(2)).uploadImage(any(MultipartFile.class));
    }
    
    private MultipartFile createMockMultipartFile(String filename) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(1024L);
        return file;
    }
}
```

## 4. Infrastructure Layer Tests

### Repository Tests

```java
/**
 * Integration tests for UserRepository.
 * Tests database operations and custom queries.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        User user = TestDataBuilder.user()
            .email(new Email("test@example.com"))
            .build();
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> result = userRepository.findByEmail(new Email("test@example.com"));
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail().getValue()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Should check email existence")
    void shouldCheckEmailExistence() {
        // Given
        User user = TestDataBuilder.user()
            .email(new Email("test@example.com"))
            .build();
        entityManager.persistAndFlush(user);
        
        // When
        boolean exists = userRepository.existsByEmail(new Email("test@example.com"));
        boolean notExists = userRepository.existsByEmail(new Email("nonexistent@example.com"));
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
    
    @Test
    @DisplayName("Should find users by role")
    void shouldFindUsersByRole() {
        // Given
        User customer = TestDataBuilder.user()
            .role(UserRole.CUSTOMER)
            .build();
        User admin = TestDataBuilder.user()
            .email(new Email("admin@example.com"))
            .role(UserRole.ADMIN)
            .build();
        
        entityManager.persistAndFlush(customer);
        entityManager.persistAndFlush(admin);
        
        // When
        List<User> customers = userRepository.findByRole(UserRole.CUSTOMER);
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        
        // Then
        assertThat(customers).hasSize(1);
        assertThat(admins).hasSize(1);
        assertThat(customers.get(0).getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(admins.get(0).getRole()).isEqualTo(UserRole.ADMIN);
    }
}
```

### External Service Tests

```java
/**
 * Unit tests for EmailService implementation.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    
    @Mock
    private JavaMailSender mailSender;
    
    @InjectMocks
    private EmailServiceImpl emailService;
    
    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender, "noreply@plantcommerce.com", "Plant Commerce");
    }
    
    @Test
    @DisplayName("Should send welcome email successfully")
    void shouldSendWelcomeEmailSuccessfully() throws MessagingException {
        // Given
        Email email = new Email("test@example.com");
        String userName = "John Doe";
        
        doNothing().when(mailSender).send(any(MimeMessage.class));
        
        // When
        emailService.sendWelcomeEmail(email, userName);
        
        // Then
        verify(mailSender).send(any(MimeMessage.class));
    }
    
    @Test
    @DisplayName("Should throw exception when email sending fails")
    void shouldThrowExceptionWhenEmailSendingFails() throws MessagingException {
        // Given
        Email email = new Email("test@example.com");
        String userName = "John Doe";
        
        doThrow(new MessagingException("SMTP error")).when(mailSender).send(any(MimeMessage.class));
        
        // When & Then
        EmailSendingException exception = assertThrowsWithMessage(
            EmailSendingException.class,
            "Failed to send email",
            () -> emailService.sendWelcomeEmail(email, userName)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("EMAIL_SENDING_ERROR");
    }
}
```

## 5. Controller Layer Tests

### User Controller Tests

```java
/**
 * Unit tests for UserController.
 * Tests REST endpoint behavior and response handling.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserApplicationService userApplicationService;
    
    @MockBean
    private RequestValidator requestValidator;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
            .email("test@example.com")
            .password("password123")
            .firstName("John")
            .lastName("Doe")
            .build();
        
        UserDto userDto = UserDto.builder()
            .id(1L)
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .build();
        
        when(userApplicationService.registerUser(any(RegisterUserRequest.class)))
            .thenReturn(userDto);
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.message").value("User registered successfully"));
        
        verify(userApplicationService).registerUser(any(RegisterUserRequest.class));
    }
    
    @Test
    @DisplayName("Should return validation error for invalid request")
    void shouldReturnValidationErrorForInvalidRequest() throws Exception {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
            .email("invalid-email")
            .password("")
            .build();
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
    
    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("test@example.com")
            .password("password123")
            .build();
        
        LoginResponse response = LoginResponse.builder()
            .token("jwt-token")
            .user(UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .build())
            .build();
        
        when(userApplicationService.loginUser(any(LoginRequest.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.token").value("jwt-token"))
            .andExpect(jsonPath("$.message").value("Login successful"));
    }
}
```

## 6. Integration Tests

```java
/**
 * Integration tests for the complete user registration flow.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
class UserRegistrationIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("Should complete user registration flow successfully")
    void shouldCompleteUserRegistrationFlowSuccessfully() throws Exception {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
            .email("integration@example.com")
            .password("password123")
            .firstName("Integration")
            .lastName("Test")
            .build();
        
        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/auth/register",
            request,
            String.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Verify user was saved in database
        Optional<User> savedUser = userRepository.findByEmail(new Email("integration@example.com"));
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getProfile().getFirstName()).isEqualTo("Integration");
    }
    
    @Test
    @DisplayName("Should handle duplicate email registration")
    void shouldHandleDuplicateEmailRegistration() throws Exception {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
            .email("duplicate@example.com")
            .password("password123")
            .firstName("Duplicate")
            .lastName("Test")
            .build();
        
        // Register first user
        restTemplate.postForEntity("/api/auth/register", request, String.class);
        
        // When - Try to register with same email
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/auth/register",
            request,
            String.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        String responseBody = response.getBody();
        assertThat(responseBody).contains("EMAIL_ALREADY_EXISTS");
    }
}
```

## 7. Test Configuration Files

### application-test.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  redis:
    host: localhost
    port: 6379
  
  mail:
    host: localhost
    port: 1025
    username: test
    password: test

jwt:
  secret: test-secret-key-for-testing-purposes-only
  expiration: 3600000

logging:
  level:
    com.plantcommerce: DEBUG
    org.springframework.security: DEBUG
```

### Test Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Test Dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>com.github.tomakehurst</groupId>
        <artifactId>wiremock-jre8</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

This comprehensive unit test structure provides:

1. **Layered Testing**: Tests for each layer (Domain, Application, Infrastructure, Controller)
2. **Test Data Builders**: Fluent API for creating test objects
3. **Mock Configuration**: Proper mocking of dependencies
4. **Integration Tests**: End-to-end testing of complete flows
5. **Test Configuration**: Separate test environment configuration
6. **Exception Testing**: Comprehensive testing of error scenarios
7. **Database Testing**: Integration tests with test database

The test structure follows testing best practices and ensures comprehensive coverage of the Plant E-commerce system functionality. 