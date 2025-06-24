# Dependency Injection Setup

## 1. Configuration Classes

### Main Application Configuration

```java
/**
 * Main application configuration class.
 * Configures Spring Boot application with proper component scanning and profiles.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.plantcommerce.repository")
@EntityScan(basePackages = "com.plantcommerce.domain")
@ComponentScan(basePackages = "com.plantcommerce")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class PlantEcommerceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PlantEcommerceApplication.class, args);
    }
    
    /**
     * Configures Jackson ObjectMapper for JSON serialization/deserialization.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }
    
    /**
     * Configures TaskExecutor for async operations.
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("PlantCommerce-");
        executor.initialize();
        return executor;
    }
}
```

### Security Configuration

```java
/**
 * Security configuration for the application.
 * Configures authentication, authorization, and security filters.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Configures security filter chain with proper authentication and authorization.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/plants/**").permitAll()
                .requestMatchers("/api/offers/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDeniedHandler())
            );
        
        return http.build();
    }
    
    /**
     * Configures CORS for cross-origin requests.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Configures password encoder for secure password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    /**
     * Configures authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Configures user details service for authentication.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new CustomUserDetailsService(userRepository, passwordEncoder);
    }
}
```

### Database Configuration

```java
/**
 * Database configuration for the application.
 * Configures data sources, JPA, and transaction management.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String dataSourceUrl;
    
    @Value("${spring.datasource.username}")
    private String dataSourceUsername;
    
    @Value("${spring.datasource.password}")
    private String dataSourcePassword;
    
    /**
     * Configures primary data source for PostgreSQL.
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceUrl);
        config.setUsername(dataSourceUsername);
        config.setPassword(dataSourcePassword);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("PlantCommerceHikariPool");
        
        return new HikariDataSource(config);
    }
    
    /**
     * Configures JPA properties and entity manager factory.
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter) {
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.plantcommerce.domain");
        em.setJpaVendorAdapter(jpaVendorAdapter);
        
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.jdbc.batch_size", "20");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        
        em.setJpaProperties(properties);
        return em;
    }
    
    /**
     * Configures JPA vendor adapter.
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.POSTGRESQL);
        adapter.setGenerateDdl(false);
        adapter.setShowSql(false);
        return adapter;
    }
    
    /**
     * Configures transaction manager.
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
```

### Redis Configuration

```java
/**
 * Redis configuration for caching and session management.
 */
@Configuration
@EnableCaching
public class RedisConfig {
    
    @Value("${spring.redis.host}")
    private String redisHost;
    
    @Value("${spring.redis.port}")
    private int redisPort;
    
    @Value("${spring.redis.password}")
    private String redisPassword;
    
    /**
     * Configures Redis connection factory.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword(redisPassword);
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.setValidateConnection(true);
        return factory;
    }
    
    /**
     * Configures Redis template for operations.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Configure serializers
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LazyLoadingPM.DEFAULT, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        
        return template;
    }
    
    /**
     * Configures cache manager for application caching.
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

## 2. Service Layer Configuration

### User Service Configuration

```java
/**
 * Configuration for user-related services and components.
 */
@Configuration
public class UserServiceConfig {
    
    /**
     * Configures user domain service implementation.
     */
    @Bean
    public UserDomainService userDomainService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserValidator userValidator) {
        return new UserDomainServiceImpl(userRepository, passwordEncoder, userValidator);
    }
    
    /**
     * Configures user application service implementation.
     */
    @Bean
    public UserApplicationService userApplicationService(
            UserDomainService userDomainService,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            EventPublisher eventPublisher,
            EmailService emailService) {
        return new UserApplicationServiceImpl(
            userDomainService, userRepository, jwtTokenProvider, 
            passwordEncoder, userMapper, eventPublisher, emailService);
    }
    
    /**
     * Configures user validator for business rule validation.
     */
    @Bean
    public UserValidator userValidator(UserRepository userRepository) {
        return new UserValidatorImpl(userRepository);
    }
    
    /**
     * Configures JWT token provider for authentication.
     */
    @Bean
    public JwtTokenProvider jwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") Long expiration) {
        return new JwtTokenProviderImpl(secret, expiration);
    }
    
    /**
     * Configures user mapper for entity-DTO conversion.
     */
    @Bean
    public UserMapper userMapper() {
        return new UserMapperImpl();
    }
}
```

### Plant Service Configuration

```java
/**
 * Configuration for plant-related services and components.
 */
@Configuration
public class PlantServiceConfig {
    
    /**
     * Configures plant domain service implementation.
     */
    @Bean
    public PlantDomainService plantDomainService(
            PlantRepository plantRepository,
            PlantValidator plantValidator) {
        return new PlantDomainServiceImpl(plantRepository, plantValidator);
    }
    
    /**
     * Configures plant application service implementation.
     */
    @Bean
    public PlantApplicationService plantApplicationService(
            PlantDomainService plantDomainService,
            PlantRepository plantRepository,
            PlantMapper plantMapper,
            PlantSearchService plantSearchService,
            EventPublisher eventPublisher,
            FileStorageService fileStorageService) {
        return new PlantApplicationServiceImpl(
            plantDomainService, plantRepository, plantMapper,
            plantSearchService, eventPublisher, fileStorageService);
    }
    
    /**
     * Configures plant validator for business rule validation.
     */
    @Bean
    public PlantValidator plantValidator() {
        return new PlantValidatorImpl();
    }
    
    /**
     * Configures plant search service for advanced search functionality.
     */
    @Bean
    public PlantSearchService plantSearchService(PlantRepository plantRepository) {
        return new PlantSearchServiceImpl(plantRepository);
    }
    
    /**
     * Configures plant mapper for entity-DTO conversion.
     */
    @Bean
    public PlantMapper plantMapper() {
        return new PlantMapperImpl();
    }
    
    /**
     * Configures file storage service for plant images.
     */
    @Bean
    public FileStorageService fileStorageService(
            @Value("${app.file-storage.base-url}") String baseUrl,
            @Value("${app.file-storage.upload-path}") String uploadPath) {
        return new LocalFileStorageService(baseUrl, uploadPath);
    }
}
```

### Order Service Configuration

```java
/**
 * Configuration for order-related services and components.
 */
@Configuration
public class OrderServiceConfig {
    
    /**
     * Configures order domain service implementation.
     */
    @Bean
    public OrderDomainService orderDomainService(
            OrderRepository orderRepository,
            PlantRepository plantRepository,
            UserRepository userRepository,
            OrderValidator orderValidator,
            PaymentProcessor paymentProcessor) {
        return new OrderDomainServiceImpl(
            orderRepository, plantRepository, userRepository,
            orderValidator, paymentProcessor);
    }
    
    /**
     * Configures order application service implementation.
     */
    @Bean
    public OrderApplicationService orderApplicationService(
            OrderDomainService orderDomainService,
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            PaymentService paymentService,
            EventPublisher eventPublisher,
            EmailService emailService) {
        return new OrderApplicationServiceImpl(
            orderDomainService, orderRepository, orderMapper,
            paymentService, eventPublisher, emailService);
    }
    
    /**
     * Configures order validator for business rule validation.
     */
    @Bean
    public OrderValidator orderValidator(
            PlantRepository plantRepository,
            UserRepository userRepository) {
        return new OrderValidatorImpl(plantRepository, userRepository);
    }
    
    /**
     * Configures payment processor for order payments.
     */
    @Bean
    public PaymentProcessor paymentProcessor(PaymentService paymentService) {
        return new PaymentProcessorImpl(paymentService);
    }
    
    /**
     * Configures order mapper for entity-DTO conversion.
     */
    @Bean
    public OrderMapper orderMapper() {
        return new OrderMapperImpl();
    }
    
    /**
     * Configures payment service for external payment processing.
     */
    @Bean
    public PaymentService paymentService(
            @Value("${app.payment.gateway-url}") String gatewayUrl,
            @Value("${app.payment.api-key}") String apiKey) {
        return new StripePaymentService(gatewayUrl, apiKey);
    }
}
```

## 3. External Service Configuration

### Email Service Configuration

```java
/**
 * Configuration for email service integration.
 */
@Configuration
public class EmailServiceConfig {
    
    @Value("${spring.mail.host}")
    private String mailHost;
    
    @Value("${spring.mail.port}")
    private int mailPort;
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${spring.mail.password}")
    private String mailPassword;
    
    /**
     * Configures JavaMailSender for email operations.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        
        return mailSender;
    }
    
    /**
     * Configures email service implementation.
     */
    @Bean
    public EmailService emailService(
            JavaMailSender javaMailSender,
            @Value("${app.email.from}") String fromEmail,
            @Value("${app.email.from-name}") String fromName) {
        return new EmailServiceImpl(javaMailSender, fromEmail, fromName);
    }
}
```

### Event Publishing Configuration

```java
/**
 * Configuration for event publishing and handling.
 */
@Configuration
@EnableAsync
public class EventConfig {
    
    /**
     * Configures event publisher for domain events.
     */
    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new EventPublisherImpl(applicationEventPublisher);
    }
    
    /**
     * Configures async event listener for user registration events.
     */
    @EventListener
    @Async
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        // Handle user registration event asynchronously
        log.info("User registered: {}", event.getUserId());
    }
    
    /**
     * Configures async event listener for order creation events.
     */
    @EventListener
    @Async
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        // Handle order creation event asynchronously
        log.info("Order created: {}", event.getOrderId());
    }
    
    /**
     * Configures async event listener for plant creation events.
     */
    @EventListener
    @Async
    public void handlePlantCreatedEvent(PlantCreatedEvent event) {
        // Handle plant creation event asynchronously
        log.info("Plant created: {}", event.getPlantId());
    }
}
```

## 4. Validation Configuration

```java
/**
 * Configuration for validation components.
 */
@Configuration
public class ValidationConfig {
    
    /**
     * Configures request validator for input validation.
     */
    @Bean
    public RequestValidator requestValidator() {
        return new RequestValidatorImpl();
    }
    
    /**
     * Configures method validation post processor.
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
```

## 5. Monitoring and Metrics Configuration

```java
/**
 * Configuration for application monitoring and metrics.
 */
@Configuration
@EnablePrometheusMetrics
public class MonitoringConfig {
    
    /**
     * Configures metrics registry for application metrics.
     */
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
    
    /**
     * Configures health indicators for application health checks.
     */
    @Bean
    public HealthIndicator databaseHealthIndicator(DataSource dataSource) {
        return new DataSourceHealthIndicator(dataSource, "SELECT 1");
    }
    
    /**
     * Configures health indicator for Redis.
     */
    @Bean
    public HealthIndicator redisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return new RedisHealthIndicator(redisConnectionFactory);
    }
}
```

This dependency injection setup follows Spring Boot best practices, implements proper separation of concerns, and ensures that all components are properly configured and testable. The configuration is modular, allowing for easy testing and maintenance. 