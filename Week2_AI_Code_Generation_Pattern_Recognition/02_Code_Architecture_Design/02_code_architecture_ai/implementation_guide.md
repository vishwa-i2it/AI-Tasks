# Implementation Guide - Plant E-commerce System

## Backend Implementation

### 1. API Gateway Configuration

**application.yml**
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=0
            
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**, /api/cart/**
          filters:
            - StripPrefix=0
            - name: AuthenticationFilter
            
        - id: plant-service
          uri: lb://plant-service
          predicates:
            - Path=/api/plants/**, /api/offers/**
          filters:
            - StripPrefix=0
            
        - id: admin-service
          uri: lb://plant-service
          predicates:
            - Path=/api/admin/**
          filters:
            - StripPrefix=0
            - name: AdminAuthenticationFilter

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 2. Authentication Service

**User Entity**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    private String phone;
    private String address;
    
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.CUSTOMER;
    
    private boolean isActive = true;
    private boolean emailVerified = false;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Getters, setters, constructors
}
```

**JWT Service**
```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

### 3. Plant Service

**Plant Entity**
```java
@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String scientificName;
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    private Integer stockQuantity = 0;
    private String category;
    private String careLevel;
    private String lightRequirements;
    private String waterRequirements;
    
    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();
    
    private boolean isActive = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Getters, setters, constructors
}
```

**Plant Controller**
```java
@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "*")
public class PlantController {
    
    @Autowired
    private PlantService plantService;
    
    @GetMapping
    public ResponseEntity<Page<PlantDto>> getAllPlants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        
        Page<PlantDto> plants = plantService.getPlants(page, size, category, search);
        return ResponseEntity.ok(plants);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PlantDto> getPlantById(@PathVariable Long id) {
        PlantDto plant = plantService.getPlantById(id);
        return ResponseEntity.ok(plant);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PlantDto>> searchPlants(@RequestParam String query) {
        List<PlantDto> plants = plantService.searchPlants(query);
        return ResponseEntity.ok(plants);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlantDto> createPlant(@Valid @RequestBody CreatePlantRequest request) {
        PlantDto plant = plantService.createPlant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plant);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlantDto> updatePlant(
            @PathVariable Long id, 
            @Valid @RequestBody UpdatePlantRequest request) {
        PlantDto plant = plantService.updatePlant(id, request);
        return ResponseEntity.ok(plant);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        plantService.deletePlant(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 4. Order Service

**Order Entity**
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(unique = true, nullable = false)
    private String orderNumber;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(nullable = false)
    private String shippingAddress;
    
    private String billingAddress;
    private String paymentMethod;
    private String trackingNumber;
    private String notes;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Getters, setters, constructors
}
```

## Frontend Implementation

### 1. React Web Application

**App.tsx**
```typescript
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Provider } from 'react-redux';
import { store } from './store';

// Components
import Navbar from './components/Navbar';
import Home from './pages/Home';
import PlantCatalog from './pages/PlantCatalog';
import PlantDetail from './pages/PlantDetail';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import OrderTracking from './pages/OrderTracking';
import Login from './pages/Login';
import Register from './pages/Register';
import Profile from './pages/Profile';
import AdminDashboard from './pages/AdminDashboard';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2e7d32', // Green color for plant theme
    },
    secondary: {
      main: '#8bc34a',
    },
  },
});

function App() {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Router>
          <Navbar />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/plants" element={<PlantCatalog />} />
            <Route path="/plants/:id" element={<PlantDetail />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/orders/:id" element={<OrderTracking />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/admin" element={<AdminDashboard />} />
          </Routes>
        </Router>
      </ThemeProvider>
    </Provider>
  );
}

export default App;
```

**Plant Catalog Component**
```typescript
import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardMedia,
  CardContent,
  Typography,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Pagination,
  Box,
  Chip,
} from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { fetchPlants, addToCart } from '../store/slices/plantSlice';
import { RootState } from '../store';

interface Plant {
  id: number;
  name: string;
  price: number;
  originalPrice?: number;
  imageUrls: string[];
  category: string;
  careLevel: string;
}

const PlantCatalog: React.FC = () => {
  const dispatch = useDispatch();
  const { plants, loading, totalPages } = useSelector((state: RootState) => state.plants);
  
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  
  useEffect(() => {
    dispatch(fetchPlants({ page, search, category }));
  }, [dispatch, page, search, category]);
  
  const handleAddToCart = (plant: Plant) => {
    dispatch(addToCart({ plant, quantity: 1 }));
  };
  
  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };
  
  if (loading) {
    return <div>Loading...</div>;
  }
  
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Plant Catalog
      </Typography>
      
      {/* Search and Filter */}
      <Box sx={{ mb: 3, display: 'flex', gap: 2 }}>
        <TextField
          label="Search plants"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          sx={{ flexGrow: 1 }}
        />
        <FormControl sx={{ minWidth: 200 }}>
          <InputLabel>Category</InputLabel>
          <Select
            value={category}
            label="Category"
            onChange={(e) => setCategory(e.target.value)}
          >
            <MenuItem value="">All Categories</MenuItem>
            <MenuItem value="indoor">Indoor Plants</MenuItem>
            <MenuItem value="outdoor">Outdoor Plants</MenuItem>
            <MenuItem value="succulents">Succulents</MenuItem>
            <MenuItem value="herbs">Herbs</MenuItem>
          </Select>
        </FormControl>
      </Box>
      
      {/* Plant Grid */}
      <Grid container spacing={3}>
        {plants.map((plant: Plant) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={plant.id}>
            <Card>
              <CardMedia
                component="img"
                height="200"
                image={plant.imageUrls[0] || '/placeholder-plant.jpg'}
                alt={plant.name}
              />
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {plant.name}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  {plant.category} • {plant.careLevel}
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                  <Typography variant="h6" color="primary">
                    ${plant.price}
                  </Typography>
                  {plant.originalPrice && (
                    <Typography variant="body2" color="text.secondary" sx={{ textDecoration: 'line-through' }}>
                      ${plant.originalPrice}
                    </Typography>
                  )}
                </Box>
                <Button
                  variant="contained"
                  fullWidth
                  onClick={() => handleAddToCart(plant)}
                >
                  Add to Cart
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      
      {/* Pagination */}
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <Pagination
          count={totalPages}
          page={page}
          onChange={handlePageChange}
          color="primary"
        />
      </Box>
    </Box>
  );
};

export default PlantCatalog;
```

### 2. React Native Mobile Application

**App.tsx (React Native)**
```typescript
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Provider } from 'react-redux';
import { store } from './store';

// Screens
import HomeScreen from './screens/HomeScreen';
import PlantCatalogScreen from './screens/PlantCatalogScreen';
import PlantDetailScreen from './screens/PlantDetailScreen';
import CartScreen from './screens/CartScreen';
import CheckoutScreen from './screens/CheckoutScreen';
import OrderTrackingScreen from './screens/OrderTrackingScreen';
import LoginScreen from './screens/LoginScreen';
import RegisterScreen from './screens/RegisterScreen';
import ProfileScreen from './screens/ProfileScreen';

const Stack = createStackNavigator();

const App: React.FC = () => {
  return (
    <Provider store={store}>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Home">
          <Stack.Screen name="Home" component={HomeScreen} />
          <Stack.Screen name="PlantCatalog" component={PlantCatalogScreen} />
          <Stack.Screen name="PlantDetail" component={PlantDetailScreen} />
          <Stack.Screen name="Cart" component={CartScreen} />
          <Stack.Screen name="Checkout" component={CheckoutScreen} />
          <Stack.Screen name="OrderTracking" component={OrderTrackingScreen} />
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
          <Stack.Screen name="Profile" component={ProfileScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    </Provider>
  );
};

export default App;
```

## Database Configuration

**application.yml (Database)**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/plant_ecommerce
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

## Docker Configuration

**docker-compose.yml**
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: plant_ecommerce
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: docker

  auth-service:
    build: ./auth-service
    ports:
      - "8081:8081"
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: docker

  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: docker

  plant-service:
    build: ./plant-service
    ports:
      - "8083:8083"
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: docker

volumes:
  postgres_data:
  redis_data:
```

## Security Configuration

**SecurityConfig.java**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    
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
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

This implementation guide provides the core structure and sample code for building the Plant E-commerce Order Management system. The architecture is designed to be scalable, secure, and maintainable while meeting all the specified requirements. 