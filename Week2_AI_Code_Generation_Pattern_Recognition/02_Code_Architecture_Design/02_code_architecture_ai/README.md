# Plant E-commerce Order Management System

## Overview

This is a comprehensive Plant E-commerce Order Management system designed to handle plant sales, order processing, and inventory management. The system supports both web and mobile applications with a robust backend architecture.

## Key Features

### For Customers
- User registration and authentication
- Browse and search plant catalog
- Shopping cart functionality
- Order placement and tracking
- Real-time order status updates
- User profile management

### For Administrators
- Plant inventory management
- Order processing and updates
- Promotional offer management
- Sales analytics and reporting
- User management

## Architecture Highlights

- **Microservices Architecture**: Separate services for authentication, orders, and plants
- **API Gateway**: Centralized request routing and authentication
- **Responsive Design**: Works seamlessly on both mobile and web browsers
- **Scalable**: Built to handle multiple concurrent users
- **Secure**: JWT-based authentication with role-based access control

## Technology Stack

### Backend
- **Java 17+** with Spring Boot 3.x
- **PostgreSQL** for primary data storage
- **Redis** for caching and sessions
- **Spring Security** for authentication
- **Spring Cloud Gateway** for API routing

### Frontend
- **React.js** for web application
- **React Native** for mobile application
- **TypeScript** for type safety
- **Material-UI** for consistent UI components

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 15 or higher
- Redis 7 or higher
- Docker (optional)

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd plant-ecommerce-system
   ```

2. **Set up the database**
   ```bash
   # Create PostgreSQL database
   createdb plant_ecommerce
   
   # Run database migrations
   # (Will be provided in the implementation)
   ```

3. **Start the backend services**
   ```bash
   # Start API Gateway
   cd api-gateway
   ./mvnw spring-boot:run
   
   # Start Auth Service
   cd ../auth-service
   ./mvnw spring-boot:run
   
   # Start Order Service
   cd ../order-service
   ./mvnw spring-boot:run
   
   # Start Plant Service
   cd ../plant-service
   ./mvnw spring-boot:run
   ```

4. **Start the frontend applications**
   ```bash
   # Web Application
   cd web-app
   npm install
   npm start
   
   # Mobile Application
   cd mobile-app
   npm install
   npx expo start
   ```

## API Documentation

Once the services are running, you can access the API documentation at:
- **API Gateway**: http://localhost:8080/swagger-ui.html
- **Auth Service**: http://localhost:8081/swagger-ui.html
- **Order Service**: http://localhost:8082/swagger-ui.html
- **Plant Service**: http://localhost:8083/swagger-ui.html

## Development

### Project Structure
```
plant-ecommerce-system/
├── api-gateway/          # Spring Cloud Gateway
├── auth-service/         # Authentication service
├── order-service/        # Order management service
├── plant-service/        # Plant catalog service
├── web-app/             # React.js web application
├── mobile-app/          # React Native mobile app
├── shared/              # Shared DTOs and utilities
└── docs/                # Documentation
```

### Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## Deployment

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d
```

### Production Considerations
- Use environment variables for configuration
- Set up proper SSL certificates
- Configure database connection pooling
- Set up monitoring and logging
- Use a CDN for static assets
- Implement proper backup strategies

## Security

- All API endpoints are protected with JWT authentication
- Admin endpoints require admin role
- Passwords are hashed using bcrypt
- CORS is properly configured
- Rate limiting is implemented
- Input validation prevents XSS and SQL injection

## Performance

- Redis caching for frequently accessed data
- Database indexing for optimal query performance
- CDN for static assets
- Image optimization for plant photos
- Lazy loading for large catalogs

## Support

For questions or issues, please create an issue in the repository or contact the development team.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Subfolders

- `java_implementation/`: Contains detailed documentation and best practices for implementing the system in Java, including design principles, error handling, dependency injection, and unit test structure. 