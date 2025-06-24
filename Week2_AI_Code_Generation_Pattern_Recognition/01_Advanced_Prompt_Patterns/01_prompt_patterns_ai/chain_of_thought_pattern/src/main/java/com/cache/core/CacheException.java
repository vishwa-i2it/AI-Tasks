package com.cache.core;

/**
 * Custom exception for cache-related errors.
 * Extends RuntimeException to avoid forcing try-catch blocks on cache operations.
 */
public class CacheException extends RuntimeException {
    
    private final CacheErrorType errorType;
    
    /**
     * Enumeration of cache error types for better error handling.
     */
    public enum CacheErrorType {
        CONNECTION_FAILED("Cache connection failed"),
        OPERATION_FAILED("Cache operation failed"),
        SERIALIZATION_ERROR("Failed to serialize/deserialize data"),
        INVALID_KEY("Invalid cache key provided"),
        INVALID_VALUE("Invalid cache value provided"),
        TIMEOUT("Cache operation timed out"),
        MEMORY_FULL("Cache memory is full"),
        UNKNOWN("Unknown cache error");
        
        private final String description;
        
        CacheErrorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Constructs a CacheException with a message and error type.
     * 
     * @param message the error message
     * @param errorType the type of cache error
     */
    public CacheException(String message, CacheErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    /**
     * Constructs a CacheException with a message, cause, and error type.
     * 
     * @param message the error message
     * @param cause the cause of the exception
     * @param errorType the type of cache error
     */
    public CacheException(String message, Throwable cause, CacheErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    /**
     * Constructs a CacheException with a cause and error type.
     * 
     * @param cause the cause of the exception
     * @param errorType the type of cache error
     */
    public CacheException(Throwable cause, CacheErrorType errorType) {
        super(cause);
        this.errorType = errorType;
    }
    
    /**
     * Gets the error type of this cache exception.
     * 
     * @return the error type
     */
    public CacheErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Gets a user-friendly error description.
     * 
     * @return the error description
     */
    public String getErrorDescription() {
        return errorType.getDescription();
    }
} 