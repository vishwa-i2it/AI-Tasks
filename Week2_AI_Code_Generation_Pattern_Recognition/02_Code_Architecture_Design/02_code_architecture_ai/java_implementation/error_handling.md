# Error Handling Strategy

## 1. Exception Hierarchy

### Base Exception Classes

```java
/**
 * Base exception for all application exceptions.
 * Provides common functionality and error code structure.
 */
public abstract class PlantCommerceException extends RuntimeException {
    
    private final String errorCode;
    private final String userMessage;
    private final ErrorSeverity severity;
    private final Map<String, Object> context;
    
    protected PlantCommerceException(String message, String errorCode, String userMessage, ErrorSeverity severity) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.severity = severity;
        this.context = new HashMap<>();
    }
    
    protected PlantCommerceException(String message, String errorCode, String userMessage, 
                                   ErrorSeverity severity, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.severity = severity;
        this.context = new HashMap<>();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    public ErrorSeverity getSeverity() {
        return severity;
    }
    
    public Map<String, Object> getContext() {
        return Collections.unmodifiableMap(context);
    }
    
    public void addContext(String key, Object value) {
        this.context.put(key, value);
    }
}

/**
 * Exception for validation errors.
 * Used when input data fails business rule validation.
 */
public abstract class ValidationException extends PlantCommerceException {
    
    protected ValidationException(String message, String errorCode, String userMessage) {
        super(message, errorCode, userMessage, ErrorSeverity.WARNING);
    }
    
    protected ValidationException(String message, String errorCode, String userMessage, Throwable cause) {
        super(message, errorCode, userMessage, ErrorSeverity.WARNING, cause);
    }
}

/**
 * Exception for business logic errors.
 * Used when business operations fail due to business rules.
 */
public abstract class BusinessException extends PlantCommerceException {
    
    protected BusinessException(String message, String errorCode, String userMessage) {
        super(message, errorCode, userMessage, ErrorSeverity.ERROR);
    }
    
    protected BusinessException(String message, String errorCode, String userMessage, Throwable cause) {
        super(message, errorCode, userMessage, ErrorSeverity.ERROR, cause);
    }
}

/**
 * Exception for system/infrastructure errors.
 * Used when external systems or infrastructure fail.
 */
public abstract class SystemException extends PlantCommerceException {
    
    protected SystemException(String message, String errorCode, String userMessage) {
        super(message, errorCode, userMessage, ErrorSeverity.CRITICAL);
    }
    
    protected SystemException(String message, String errorCode, String userMessage, Throwable cause) {
        super(message, errorCode, userMessage, ErrorSeverity.CRITICAL, cause);
    }
}
```

### Domain-Specific Exceptions

```java
// User Domain Exceptions
public class UserRegistrationException extends ValidationException {
    public UserRegistrationException(String message) {
        super(message, "USER_REGISTRATION_ERROR", "User registration failed. Please check your information.");
    }
}

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email, "EMAIL_ALREADY_EXISTS", 
              "An account with this email already exists.");
        addContext("email", email);
    }
}

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND", "User not found.");
    }
    
    public UserNotFoundException(UserId userId) {
        super("User not found with ID: " + userId, "USER_NOT_FOUND", "User not found.");
        addContext("userId", userId);
    }
}

public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_ERROR", "Invalid credentials.");
    }
}

public class UserAccountLockedException extends BusinessException {
    public UserAccountLockedException(String message) {
        super(message, "ACCOUNT_LOCKED", "Your account has been locked. Please contact support.");
    }
}

public class PasswordValidationException extends ValidationException {
    public PasswordValidationException(String message) {
        super(message, "PASSWORD_VALIDATION_ERROR", "Password does not meet requirements.");
    }
}

// Plant Domain Exceptions
public class PlantValidationException extends ValidationException {
    public PlantValidationException(String message) {
        super(message, "PLANT_VALIDATION_ERROR", "Plant information is invalid.");
    }
}

public class PlantNotFoundException extends BusinessException {
    public PlantNotFoundException(String message) {
        super(message, "PLANT_NOT_FOUND", "Plant not found.");
    }
    
    public PlantNotFoundException(PlantId plantId) {
        super("Plant not found with ID: " + plantId, "PLANT_NOT_FOUND", "Plant not found.");
        addContext("plantId", plantId);
    }
}

public class DuplicatePlantException extends BusinessException {
    public DuplicatePlantException(String plantName) {
        super("Plant with name already exists: " + plantName, "DUPLICATE_PLANT", 
              "A plant with this name already exists.");
        addContext("plantName", plantName);
    }
}

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message, "INSUFFICIENT_STOCK", "Insufficient stock available.");
    }
    
    public InsufficientStockException(PlantId plantId, int requested, int available) {
        super("Insufficient stock for plant: " + plantId, "INSUFFICIENT_STOCK", 
              "Insufficient stock available.");
        addContext("plantId", plantId);
        addContext("requested", requested);
        addContext("available", available);
    }
}

// Order Domain Exceptions
public class OrderValidationException extends ValidationException {
    public OrderValidationException(String message) {
        super(message, "ORDER_VALIDATION_ERROR", "Order information is invalid.");
    }
}

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(String message) {
        super(message, "ORDER_NOT_FOUND", "Order not found.");
    }
    
    public OrderNotFoundException(OrderId orderId) {
        super("Order not found with ID: " + orderId, "ORDER_NOT_FOUND", "Order not found.");
        addContext("orderId", orderId);
    }
}

public class InvalidStatusTransitionException extends BusinessException {
    public InvalidStatusTransitionException(String currentStatus, String newStatus) {
        super("Invalid status transition from " + currentStatus + " to " + newStatus, 
              "INVALID_STATUS_TRANSITION", "Invalid order status transition.");
        addContext("currentStatus", currentStatus);
        addContext("newStatus", newStatus);
    }
}

public class OrderCannotBeCancelledException extends BusinessException {
    public OrderCannotBeCancelledException(String reason) {
        super("Order cannot be cancelled: " + reason, "ORDER_CANNOT_BE_CANCELLED", 
              "This order cannot be cancelled.");
        addContext("reason", reason);
    }
}

// Payment Domain Exceptions
public class PaymentProcessingException extends SystemException {
    public PaymentProcessingException(String message) {
        super(message, "PAYMENT_PROCESSING_ERROR", "Payment processing failed.");
    }
    
    public PaymentProcessingException(String message, Throwable cause) {
        super(message, "PAYMENT_PROCESSING_ERROR", "Payment processing failed.", cause);
    }
}

public class InvalidPaymentMethodException extends ValidationException {
    public InvalidPaymentMethodException(String paymentMethod) {
        super("Invalid payment method: " + paymentMethod, "INVALID_PAYMENT_METHOD", 
              "The selected payment method is not supported.");
        addContext("paymentMethod", paymentMethod);
    }
}

// External Service Exceptions
public class EmailSendingException extends SystemException {
    public EmailSendingException(String message) {
        super(message, "EMAIL_SENDING_ERROR", "Failed to send email.");
    }
    
    public EmailSendingException(String message, Throwable cause) {
        super(message, "EMAIL_SENDING_ERROR", "Failed to send email.", cause);
    }
}

public class ImageUploadException extends SystemException {
    public ImageUploadException(String message) {
        super(message, "IMAGE_UPLOAD_ERROR", "Failed to upload image.");
    }
    
    public ImageUploadException(String message, Throwable cause) {
        super(message, "IMAGE_UPLOAD_ERROR", "Failed to upload image.", cause);
    }
}
```

## 2. Error Codes and Messages

```java
/**
 * Enumeration of error codes used throughout the application.
 * Provides centralized error code management.
 */
public enum ErrorCode {
    
    // User Domain (1000-1999)
    USER_REGISTRATION_ERROR("1001", "User registration failed"),
    EMAIL_ALREADY_EXISTS("1002", "Email already exists"),
    USER_NOT_FOUND("1003", "User not found"),
    AUTHENTICATION_ERROR("1004", "Authentication failed"),
    ACCOUNT_LOCKED("1005", "Account is locked"),
    PASSWORD_VALIDATION_ERROR("1006", "Password validation failed"),
    INVALID_RESET_TOKEN("1007", "Invalid or expired reset token"),
    PROFILE_UPDATE_ERROR("1008", "Profile update failed"),
    
    // Plant Domain (2000-2999)
    PLANT_VALIDATION_ERROR("2001", "Plant validation failed"),
    PLANT_NOT_FOUND("2002", "Plant not found"),
    DUPLICATE_PLANT("2003", "Plant already exists"),
    INSUFFICIENT_STOCK("2004", "Insufficient stock"),
    OFFER_VALIDATION_ERROR("2005", "Offer validation failed"),
    OFFER_NOT_FOUND("2006", "Offer not found"),
    
    // Order Domain (3000-3999)
    ORDER_VALIDATION_ERROR("3001", "Order validation failed"),
    ORDER_NOT_FOUND("3002", "Order not found"),
    INVALID_STATUS_TRANSITION("3003", "Invalid status transition"),
    ORDER_CANNOT_BE_CANCELLED("3004", "Order cannot be cancelled"),
    
    // Payment Domain (4000-4999)
    PAYMENT_PROCESSING_ERROR("4001", "Payment processing failed"),
    INVALID_PAYMENT_METHOD("4002", "Invalid payment method"),
    PAYMENT_NOT_FOUND("4003", "Payment not found"),
    REFUND_PROCESSING_ERROR("4004", "Refund processing failed"),
    
    // External Services (5000-5999)
    EMAIL_SENDING_ERROR("5001", "Email sending failed"),
    IMAGE_UPLOAD_ERROR("5002", "Image upload failed"),
    IMAGE_DELETION_ERROR("5003", "Image deletion failed"),
    IMAGE_NOT_FOUND("5004", "Image not found"),
    INVALID_FILE_TYPE("5005", "Invalid file type"),
    
    // System Errors (9000-9999)
    INTERNAL_SERVER_ERROR("9001", "Internal server error"),
    DATABASE_ERROR("9002", "Database error"),
    CACHE_ERROR("9003", "Cache error"),
    EXTERNAL_SERVICE_ERROR("9004", "External service error");
    
    private final String code;
    private final String defaultMessage;
    
    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
}

/**
 * Enumeration of error severity levels.
 */
public enum ErrorSeverity {
    INFO("Information"),
    WARNING("Warning"),
    ERROR("Error"),
    CRITICAL("Critical");
    
    private final String description;
    
    ErrorSeverity(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
```

## 3. Global Exception Handler

```java
/**
 * Global exception handler for REST API endpoints.
 * Provides consistent error responses across the application.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    private final ErrorResponseBuilder errorResponseBuilder;
    private final MetricsService metricsService;
    
    public GlobalExceptionHandler(ErrorResponseBuilder errorResponseBuilder, MetricsService metricsService) {
        this.errorResponseBuilder = errorResponseBuilder;
        this.metricsService = metricsService;
    }
    
    /**
     * Handles PlantCommerceException and its subclasses.
     */
    @ExceptionHandler(PlantCommerceException.class)
    public ResponseEntity<ErrorResponse> handlePlantCommerceException(PlantCommerceException ex, HttpServletRequest request) {
        log.error("PlantCommerceException occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = errorResponseBuilder.build(ex, request);
        metricsService.incrementErrorCounter(ex.getErrorCode(), ex.getSeverity());
        
        HttpStatus status = determineHttpStatus(ex);
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handles validation exceptions from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        ErrorResponse errorResponse = errorResponseBuilder.buildValidationError(errors, request);
        metricsService.incrementErrorCounter("VALIDATION_ERROR", ErrorSeverity.WARNING);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handles constraint violation exceptions.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());
        
        List<String> errors = ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.toList());
        
        ErrorResponse errorResponse = errorResponseBuilder.buildValidationError(errors, request);
        metricsService.incrementErrorCounter("CONSTRAINT_VIOLATION", ErrorSeverity.WARNING);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handles JWT authentication exceptions.
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex, HttpServletRequest request) {
        log.warn("JWT error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = errorResponseBuilder.buildAuthenticationError(ex.getMessage(), request);
        metricsService.incrementErrorCounter("JWT_ERROR", ErrorSeverity.ERROR);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handles access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = errorResponseBuilder.buildAccessDeniedError(request);
        metricsService.incrementErrorCounter("ACCESS_DENIED", ErrorSeverity.ERROR);
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * Handles database exceptions.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        log.error("Database error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = errorResponseBuilder.buildSystemError("Database operation failed", request);
        metricsService.incrementErrorCounter("DATABASE_ERROR", ErrorSeverity.CRITICAL);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = errorResponseBuilder.buildSystemError("An unexpected error occurred", request);
        metricsService.incrementErrorCounter("UNEXPECTED_ERROR", ErrorSeverity.CRITICAL);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Determines HTTP status code based on exception type and severity.
     */
    private HttpStatus determineHttpStatus(PlantCommerceException ex) {
        if (ex instanceof ValidationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof UserNotFoundException || ex instanceof PlantNotFoundException || ex instanceof OrderNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof SystemException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }
}
```

## 4. Error Response Builder

```java
/**
 * Builder for creating consistent error responses.
 */
@Component
public class ErrorResponseBuilder {
    
    private final ObjectMapper objectMapper;
    
    public ErrorResponseBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Builds error response from PlantCommerceException.
     */
    public ErrorResponse build(PlantCommerceException ex, HttpServletRequest request) {
        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(determineHttpStatus(ex).value())
            .error(ex.getSeverity().getDescription())
            .errorCode(ex.getErrorCode())
            .message(ex.getUserMessage())
            .path(request.getRequestURI())
            .method(request.getMethod())
            .context(ex.getContext())
            .build();
    }
    
    /**
     * Builds validation error response.
     */
    public ErrorResponse buildValidationError(List<String> errors, HttpServletRequest request) {
        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .errorCode("VALIDATION_ERROR")
            .message("Request validation failed")
            .path(request.getRequestURI())
            .method(request.getMethod())
            .context(Map.of("validationErrors", errors))
            .build();
    }
    
    /**
     * Builds authentication error response.
     */
    public ErrorResponse buildAuthenticationError(String message, HttpServletRequest request) {
        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Authentication Error")
            .errorCode("AUTHENTICATION_ERROR")
            .message(message)
            .path(request.getRequestURI())
            .method(request.getMethod())
            .build();
    }
    
    /**
     * Builds access denied error response.
     */
    public ErrorResponse buildAccessDeniedError(HttpServletRequest request) {
        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Access Denied")
            .errorCode("ACCESS_DENIED")
            .message("You don't have permission to access this resource")
            .path(request.getRequestURI())
            .method(request.getMethod())
            .build();
    }
    
    /**
     * Builds system error response.
     */
    public ErrorResponse buildSystemError(String message, HttpServletRequest request) {
        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("System Error")
            .errorCode("SYSTEM_ERROR")
            .message(message)
            .path(request.getRequestURI())
            .method(request.getMethod())
            .build();
    }
    
    private HttpStatus determineHttpStatus(PlantCommerceException ex) {
        if (ex instanceof ValidationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof UserNotFoundException || ex instanceof PlantNotFoundException || ex instanceof OrderNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof SystemException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }
}

/**
 * Error response DTO for consistent error responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
    private String method;
    private Map<String, Object> context;
}
```

## 5. Error Monitoring and Metrics

```java
/**
 * Service for tracking error metrics and monitoring.
 */
@Service
@Slf4j
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    private final Counter errorCounter;
    private final Timer errorTimer;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.errorCounter = Counter.builder("plant_commerce_errors_total")
            .description("Total number of errors")
            .register(meterRegistry);
        this.errorTimer = Timer.builder("plant_commerce_error_duration")
            .description("Error handling duration")
            .register(meterRegistry);
    }
    
    /**
     * Increments error counter for specific error code and severity.
     */
    public void incrementErrorCounter(String errorCode, ErrorSeverity severity) {
        errorCounter.increment(Tags.of(
            "error_code", errorCode,
            "severity", severity.name()
        ));
        
        log.error("Error occurred - Code: {}, Severity: {}", errorCode, severity);
    }
    
    /**
     * Records error handling duration.
     */
    public void recordErrorDuration(Duration duration, String errorCode) {
        errorTimer.record(duration, Tags.of("error_code", errorCode));
    }
    
    /**
     * Records error with additional context.
     */
    public void recordError(String errorCode, ErrorSeverity severity, Map<String, Object> context) {
        incrementErrorCounter(errorCode, severity);
        
        // Log additional context for debugging
        if (!context.isEmpty()) {
            log.error("Error context: {}", context);
        }
    }
}
```

This comprehensive error handling strategy provides:

1. **Structured Exception Hierarchy**: Clear categorization of exceptions by type and severity
2. **Consistent Error Responses**: Standardized error response format across the application
3. **Global Exception Handling**: Centralized exception handling for all REST endpoints
4. **Error Monitoring**: Metrics and logging for error tracking and debugging
5. **User-Friendly Messages**: Separate technical and user-facing error messages
6. **Context Preservation**: Additional context for debugging and monitoring

The strategy follows best practices for error handling in Spring Boot applications and provides a robust foundation for error management in the Plant E-commerce system. 