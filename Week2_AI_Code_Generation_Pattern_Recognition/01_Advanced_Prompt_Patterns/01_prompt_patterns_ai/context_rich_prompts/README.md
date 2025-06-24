# Plant Sales Web Application

A comprehensive plant sales web application built with Java Spring Boot (backend) and React (frontend).

## Step 1: Core Data Models and Database Schema

### Overview

The application uses a well-structured database schema with JPA entities to manage plant sales, user accounts, orders, and advanced features like plant care reminders.

### Database Schema Design

#### Core Entities

1. **Plant** - Central entity for plant catalog
2. **PlantCategory** - Plant categorization
3. **User** - User accounts and authentication
4. **Order** - Order management
5. **OrderItem** - Individual items in orders
6. **ShoppingCart** - User shopping carts
7. **CartItem** - Items in shopping carts
8. **Review** - Customer reviews and ratings
9. **Address** - User addresses for shipping/billing
10. **PlantCareReminder** - Plant care notifications

### Entity Relationships

```
User (1) ←→ (N) Order
User (1) ←→ (1) ShoppingCart
User (1) ←→ (N) Review
User (1) ←→ (N) Address
User (1) ←→ (N) PlantCareReminder

Plant (1) ←→ (N) OrderItem
Plant (1) ←→ (N) Review
Plant (1) ←→ (N) CartItem
Plant (1) ←→ (N) PlantCareReminder
Plant (N) ←→ (1) PlantCategory

Order (1) ←→ (N) OrderItem
ShoppingCart (1) ←→ (N) CartItem
```

### Key Design Decisions

#### 1. Plant Entity Design
- **Comprehensive Plant Information**: Includes scientific name, care instructions, pricing, and inventory
- **Care Level Enums**: EASY, MEDIUM, HARD, EXPERT for user guidance
- **Light Requirements**: LOW, MEDIUM, HIGH, DIRECT_SUN for proper plant placement
- **Inventory Management**: Stock tracking with minimum stock levels
- **Pet Safety**: Boolean flag for pet-friendly plants
- **Indoor/Outdoor Classification**: Separate flags for placement guidance

#### 2. User Management
- **Role-Based Access**: CUSTOMER, ADMIN, MODERATOR roles
- **Email Verification**: Token-based email verification system
- **Password Reset**: Secure password reset functionality
- **Address Management**: Multiple addresses per user with type classification

#### 3. Order Management
- **Order Status Tracking**: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
- **Payment Status**: Separate tracking for payment processing
- **Order Number Generation**: Unique order numbers for tracking
- **Flexible Pricing**: Support for subtotal, tax, shipping, and discounts

#### 4. Shopping Cart Design
- **Session-Based**: Each user has one shopping cart
- **Item Persistence**: Cart items persist across sessions
- **Price Snapshot**: Stores plant price at time of addition to prevent price changes

#### 5. Review System
- **Rating System**: 1-5 star ratings with title and content
- **Verification**: Verified purchase indicators
- **Moderation**: Approval system for review management
- **Helpful Votes**: Community-driven helpfulness tracking

#### 6. Plant Care Reminders
- **Multiple Reminder Types**: Watering, fertilizing, pruning, repotting, pest checks
- **Flexible Scheduling**: Configurable frequency in days
- **Due Date Tracking**: Next reminder date calculation
- **Completion Tracking**: Last reminder date for history

### Database Schema Details

#### Plant Table
```sql
CREATE TABLE plants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    scientific_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL,
    min_stock_level INT DEFAULT 5,
    care_level ENUM('EASY', 'MEDIUM', 'HARD', 'EXPERT') DEFAULT 'EASY',
    light_requirement ENUM('LOW', 'MEDIUM', 'HIGH', 'DIRECT_SUN') DEFAULT 'MEDIUM',
    water_frequency_days INT DEFAULT 7,
    fertilizer_frequency_days INT DEFAULT 30,
    care_instructions TEXT,
    max_height_cm INT,
    max_width_cm INT,
    pot_size_cm INT,
    is_pet_friendly BOOLEAN DEFAULT FALSE,
    is_indoor BOOLEAN DEFAULT TRUE,
    is_outdoor BOOLEAN DEFAULT FALSE,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES plant_categories(id)
);
```

#### User Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    date_of_birth TIMESTAMP,
    role ENUM('CUSTOMER', 'ADMIN', 'MODERATOR') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expiry TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

#### Order Table
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2),
    tax_amount DECIMAL(10,2),
    shipping_amount DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    payment_method VARCHAR(100),
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED') DEFAULT 'PENDING',
    shipping_address TEXT,
    billing_address TEXT,
    tracking_number VARCHAR(255),
    notes TEXT,
    order_date TIMESTAMP NOT NULL,
    shipped_date TIMESTAMP,
    delivered_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Business Logic Implementation

#### Inventory Management
- **Stock Tracking**: Real-time stock quantity updates
- **Low Stock Alerts**: Automatic detection when stock falls below minimum
- **Stock Reservation**: Prevents overselling during checkout process

#### Order Processing
- **Order Validation**: Ensures sufficient stock before order confirmation
- **Price Calculation**: Automatic calculation of totals, taxes, and discounts
- **Status Management**: Proper order status transitions with validation

#### User Experience Features
- **Shopping Cart Persistence**: Cart items saved across sessions
- **Address Management**: Multiple addresses with default selection
- **Review System**: Comprehensive rating and review functionality
- **Care Reminders**: Automated plant care notifications

### Potential Issues and Edge Cases

#### 1. Concurrency Issues
- **Stock Race Conditions**: Multiple users purchasing same item simultaneously
- **Solution**: Implement optimistic locking or database-level constraints

#### 2. Data Integrity
- **Orphaned Records**: Cart items without valid plants
- **Solution**: Proper cascade delete configurations and foreign key constraints

#### 3. Performance Considerations
- **Large Datasets**: Plant catalog with thousands of items
- **Solution**: Implement pagination, indexing, and lazy loading

#### 4. Security Concerns
- **SQL Injection**: Prevented through JPA parameterized queries
- **XSS Attacks**: Input validation and output encoding required
- **Authentication**: JWT-based secure authentication system

### Improvements and Best Practices

#### 1. Database Optimization
- **Indexing**: Add indexes on frequently queried columns
- **Partitioning**: Consider table partitioning for large datasets
- **Caching**: Implement Redis caching for frequently accessed data

#### 2. Security Enhancements
- **Password Hashing**: Use BCrypt for password encryption
- **Input Validation**: Comprehensive validation at all layers
- **Rate Limiting**: Prevent abuse of API endpoints

#### 3. Scalability
- **Microservices**: Consider breaking into smaller services
- **Load Balancing**: Implement for high-traffic scenarios
- **Database Sharding**: For very large datasets

#### 4. Monitoring and Logging
- **Application Metrics**: Implement health checks and monitoring
- **Audit Logging**: Track important business events
- **Error Tracking**: Comprehensive error logging and alerting

### Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0, Spring Data JPA
- **Database**: H2 (development), PostgreSQL (production)
- **Authentication**: JWT tokens with Spring Security
- **Validation**: Bean Validation (Jakarta Validation)
- **Build Tool**: Maven
- **Frontend**: React (to be implemented in Step 2)

This database schema provides a solid foundation for a comprehensive plant sales application with room for future enhancements and scalability.

## 📝 Prompt Examples

### 1. Basic Prompt
```
Create a REST API to manage plants in an online store.
```

### 2. Chain-of-Thought Prompt
```
I need to build a REST API for plant management. Let me break this down:
Step 1: Design the Plant entity with fields for name, price, and stock.
Step 2: Implement CRUD endpoints for plants.
Step 3: Add input validation and error handling.
Step 4: Write tests for each endpoint.
For each step, please:
- Explain the approach
- Write the code
- Suggest improvements
Start with Step 1.
```

### 3. Context-Rich Prompt
```
Context: I am building a plant sales web application using Java Spring Boot. The application should support plant catalog management, user authentication, and order processing. Security and scalability are important.

Current Architecture: Backend is Java Spring Boot with JPA, frontend is React. Database is MySQL.

Technology Stack: Java 11+, Spring Boot, JPA, MySQL, React

Constraints: Must follow RESTful best practices, include input validation, and support role-based access control. Performance and security are priorities.

Task: Design and implement the Plant entity and its REST API endpoints, including validation and error handling.

Expected Output: Java code for the Plant entity, repository, service, controller, and example API requests/responses.

Quality Criteria: Code should be clean, well-documented, tested, and follow best practices for security and scalability.
``` 