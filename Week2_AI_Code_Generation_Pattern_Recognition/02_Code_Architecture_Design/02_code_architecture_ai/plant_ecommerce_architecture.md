# Plant E-commerce Order Management System Architecture

## 1. High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              PLANT E-COMMERCE SYSTEM                            │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MOBILE APP    │    │   WEB BROWSER   │    │   ADMIN PANEL   │
│   (React Native)│    │   (React.js)    │    │   (React.js)    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │      API GATEWAY          │
                    │   (Spring Cloud Gateway)  │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │    LOAD BALANCER          │
                    │   (Nginx/HAProxy)         │
                    └─────────────┬─────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
┌─────────▼─────────┐  ┌─────────▼─────────┐  ┌─────────▼─────────┐
│   AUTH SERVICE    │  │   ORDER SERVICE   │  │   PLANT SERVICE   │
│   (Spring Boot)   │  │   (Spring Boot)   │  │   (Spring Boot)   │
└─────────┬─────────┘  └─────────┬─────────┘  └─────────┬─────────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │      DATABASE LAYER       │
                    │   (PostgreSQL + Redis)    │
                    └───────────────────────────┘
```

## 2. Component Breakdown with Responsibilities

### Frontend Components

#### Mobile App (React Native)
- **User Registration/Login**: Handle user authentication
- **Plant Catalog**: Browse and search plants
- **Shopping Cart**: Add/remove items, quantity management
- **Order Placement**: Checkout process and payment integration
- **Order Tracking**: Real-time order status updates
- **User Profile**: Account management and order history

#### Web Browser (React.js)
- **Responsive Design**: Optimized for desktop and tablet
- **Enhanced Catalog**: Advanced filtering and sorting
- **Detailed Product Views**: High-resolution images and descriptions
- **Admin Dashboard**: Plant management and order processing

#### Admin Panel (React.js)
- **Plant Management**: CRUD operations for plants
- **Offer Management**: Create and update promotional offers
- **Order Management**: View, update, and process orders
- **Analytics Dashboard**: Sales reports and user analytics
- **Inventory Management**: Stock tracking and alerts

### Backend Services

#### API Gateway (Spring Cloud Gateway)
- **Request Routing**: Route requests to appropriate services
- **Authentication**: JWT token validation
- **Rate Limiting**: Prevent API abuse
- **CORS Management**: Handle cross-origin requests
- **Load Balancing**: Distribute traffic across services

#### Authentication Service
- **User Registration**: Email verification and account creation
- **User Login**: JWT token generation and validation
- **Password Management**: Secure password hashing and reset
- **Role Management**: User roles (Customer, Admin)
- **Session Management**: Token refresh and logout

#### Order Service
- **Order Creation**: Process checkout and create orders
- **Order Tracking**: Real-time status updates
- **Payment Integration**: Payment processing and confirmation
- **Order History**: User order retrieval
- **Order Updates**: Admin order modification

#### Plant Service
- **Plant Catalog**: CRUD operations for plants
- **Search & Filter**: Advanced search functionality
- **Inventory Management**: Stock tracking and updates
- **Offer Management**: Promotional offers and discounts
- **Image Management**: Plant image upload and storage

### Infrastructure Components

#### Database Layer
- **PostgreSQL**: Primary database for structured data
- **Redis**: Caching and session storage
- **File Storage**: Plant images and documents

#### Load Balancer
- **Traffic Distribution**: Distribute requests across services
- **Health Checks**: Monitor service availability
- **SSL Termination**: Handle HTTPS connections

## 3. Database Schema Design

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role VARCHAR(20) DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Plants Table
```sql
CREATE TABLE plants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    scientific_name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    stock_quantity INTEGER DEFAULT 0,
    category VARCHAR(100),
    care_level VARCHAR(50),
    light_requirements VARCHAR(100),
    water_requirements VARCHAR(100),
    image_urls TEXT[], -- Array of image URLs
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Orders Table
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    shipping_address TEXT NOT NULL,
    billing_address TEXT,
    payment_method VARCHAR(50),
    tracking_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Order Items Table
```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    plant_id BIGINT REFERENCES plants(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Offers Table
```sql
CREATE TABLE offers (
    id BIGSERIAL PRIMARY KEY,
    plant_id BIGINT REFERENCES plants(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(10,2),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Order Status History Table
```sql
CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 4. API Endpoint Structure

### Authentication Endpoints
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/refresh-token
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
GET    /api/auth/profile
PUT    /api/auth/profile
```

### Plant Endpoints
```
GET    /api/plants                    # Get all plants with pagination
GET    /api/plants/{id}              # Get plant by ID
GET    /api/plants/search             # Search plants
GET    /api/plants/categories         # Get plant categories
POST   /api/admin/plants              # Create new plant (Admin)
PUT    /api/admin/plants/{id}         # Update plant (Admin)
DELETE /api/admin/plants/{id}         # Delete plant (Admin)
POST   /api/admin/plants/{id}/images  # Upload plant images (Admin)
```

### Order Endpoints
```
GET    /api/orders                    # Get user orders
GET    /api/orders/{id}              # Get order by ID
POST   /api/orders                    # Create new order
PUT    /api/orders/{id}/status        # Update order status (Admin)
GET    /api/admin/orders              # Get all orders (Admin)
PUT    /api/admin/orders/{id}         # Update order details (Admin)
```

### Offer Endpoints
```
GET    /api/offers                    # Get active offers
GET    /api/offers/{id}              # Get offer by ID
POST   /api/admin/offers              # Create new offer (Admin)
PUT    /api/admin/offers/{id}         # Update offer (Admin)
DELETE /api/admin/offers/{id}         # Delete offer (Admin)
```

### Cart Endpoints
```
GET    /api/cart                      # Get user cart
POST   /api/cart/items                # Add item to cart
PUT    /api/cart/items/{id}           # Update cart item
DELETE /api/cart/items/{id}           # Remove item from cart
DELETE /api/cart                      # Clear cart
```

## 5. Technology Stack Recommendations

### Backend (Java)
- **Framework**: Spring Boot 3.x
- **API Gateway**: Spring Cloud Gateway
- **Database**: PostgreSQL 15+
- **Caching**: Redis 7+
- **Authentication**: Spring Security + JWT
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Testing**: JUnit 5, Mockito, TestContainers
- **Build Tool**: Maven or Gradle
- **Containerization**: Docker
- **Orchestration**: Kubernetes (for production)

### Frontend Recommendations

#### Web Application (React.js)
- **Framework**: React 18+ with TypeScript
- **State Management**: Redux Toolkit or Zustand
- **UI Library**: Material-UI (MUI) or Ant Design
- **Routing**: React Router v6
- **HTTP Client**: Axios or React Query
- **Form Handling**: React Hook Form
- **Styling**: Styled Components or Tailwind CSS
- **Build Tool**: Vite or Create React App
- **Testing**: Jest, React Testing Library

#### Mobile Application (React Native)
- **Framework**: React Native 0.72+
- **Navigation**: React Navigation v6
- **State Management**: Redux Toolkit or Zustand
- **UI Library**: React Native Elements or NativeBase
- **HTTP Client**: Axios
- **Storage**: AsyncStorage
- **Push Notifications**: React Native Push Notification
- **Build Tool**: Expo CLI or React Native CLI

### DevOps & Infrastructure
- **Container Registry**: Docker Hub or AWS ECR
- **CI/CD**: GitHub Actions or GitLab CI
- **Cloud Platform**: AWS, Azure, or Google Cloud
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **CDN**: CloudFront or CloudFlare
- **File Storage**: AWS S3 or similar

### Security Considerations
- **HTTPS**: SSL/TLS encryption
- **JWT**: Secure token management
- **Rate Limiting**: API abuse prevention
- **Input Validation**: XSS and SQL injection prevention
- **CORS**: Proper cross-origin configuration
- **Password Security**: Bcrypt hashing
- **API Security**: OAuth 2.0 or API keys for admin endpoints

This architecture provides a scalable, secure, and performant solution for the Plant E-commerce Order Management system, meeting all the specified functional and non-functional requirements. 