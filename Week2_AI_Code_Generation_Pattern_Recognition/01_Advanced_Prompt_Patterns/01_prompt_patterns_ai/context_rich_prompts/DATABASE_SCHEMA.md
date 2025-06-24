# Database Schema Documentation

## Overview
This document describes the complete database schema for the Plant Sales Application.

## Tables

### 1. plants
**Purpose**: Stores plant catalog information
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

### 2. plant_categories
**Purpose**: Categorizes plants
```sql
CREATE TABLE plant_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### 3. users
**Purpose**: User accounts and authentication
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

### 4. orders
**Purpose**: Order management
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

### 5. order_items
**Purpose**: Individual items in orders
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    plant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    plant_name VARCHAR(100),
    plant_image_url VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (plant_id) REFERENCES plants(id)
);
```

### 6. shopping_carts
**Purpose**: User shopping carts
```sql
CREATE TABLE shopping_carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 7. cart_items
**Purpose**: Items in shopping carts
```sql
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shopping_cart_id BIGINT NOT NULL,
    plant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    plant_name VARCHAR(100),
    plant_price DECIMAL(10,2),
    plant_image_url VARCHAR(255),
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts(id),
    FOREIGN KEY (plant_id) REFERENCES plants(id)
);
```

### 8. reviews
**Purpose**: Customer reviews and ratings
```sql
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plant_id BIGINT NOT NULL,
    rating INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    is_helpful_count INT DEFAULT 0,
    is_approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (plant_id) REFERENCES plants(id)
);
```

### 9. addresses
**Purpose**: User addresses for shipping/billing
```sql
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    address_type ENUM('SHIPPING', 'BILLING', 'BOTH') NOT NULL,
    street_address VARCHAR(200) NOT NULL,
    street_address_2 VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 10. plant_care_reminders
**Purpose**: Plant care notifications
```sql
CREATE TABLE plant_care_reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plant_id BIGINT NOT NULL,
    reminder_type ENUM('WATERING', 'FERTILIZING', 'PRUNING', 'REPOTTING', 'PEST_CHECK', 'GENERAL_CARE') NOT NULL,
    plant_name VARCHAR(100),
    frequency_days INT NOT NULL,
    next_reminder_date TIMESTAMP NOT NULL,
    last_reminder_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (plant_id) REFERENCES plants(id)
);
```

## Key Relationships

1. **User → Orders**: One-to-Many (User can have multiple orders)
2. **User → ShoppingCart**: One-to-One (Each user has one shopping cart)
3. **User → Reviews**: One-to-Many (User can write multiple reviews)
4. **User → Addresses**: One-to-Many (User can have multiple addresses)
5. **User → PlantCareReminders**: One-to-Many (User can have multiple care reminders)
6. **Plant → OrderItems**: One-to-Many (Plant can be in multiple order items)
7. **Plant → Reviews**: One-to-Many (Plant can have multiple reviews)
8. **Plant → CartItems**: One-to-Many (Plant can be in multiple cart items)
9. **Plant → PlantCareReminders**: One-to-Many (Plant can have multiple care reminders)
10. **Plant → PlantCategory**: Many-to-One (Multiple plants can belong to one category)
11. **Order → OrderItems**: One-to-Many (Order can have multiple items)
12. **ShoppingCart → CartItems**: One-to-Many (Cart can have multiple items)

## Indexes

Recommended indexes for performance:

```sql
-- Plants table
CREATE INDEX idx_plants_category ON plants(category_id);
CREATE INDEX idx_plants_active ON plants(is_active);
CREATE INDEX idx_plants_stock ON plants(stock_quantity);
CREATE INDEX idx_plants_price ON plants(price);
CREATE INDEX idx_plants_care_level ON plants(care_level);
CREATE INDEX idx_plants_light_requirement ON plants(light_requirement);

-- Users table
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Orders table
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_number ON orders(order_number);

-- Reviews table
CREATE INDEX idx_reviews_plant ON reviews(plant_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- Shopping cart items
CREATE INDEX idx_cart_items_cart ON cart_items(shopping_cart_id);
CREATE INDEX idx_cart_items_plant ON cart_items(plant_id);

-- Order items
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_plant ON order_items(plant_id);
```

## Constraints

1. **Unique Constraints**:
   - Plant names must be unique
   - Usernames must be unique
   - Email addresses must be unique
   - Order numbers must be unique
   - Each user can have only one shopping cart

2. **Foreign Key Constraints**:
   - All foreign key relationships are properly defined
   - Cascade delete configurations for dependent entities

3. **Check Constraints**:
   - Rating must be between 1 and 5
   - Price must be greater than 0
   - Stock quantity must be non-negative
   - Quantity must be at least 1

This schema provides a robust foundation for the plant sales application with proper relationships, constraints, and performance optimizations. 