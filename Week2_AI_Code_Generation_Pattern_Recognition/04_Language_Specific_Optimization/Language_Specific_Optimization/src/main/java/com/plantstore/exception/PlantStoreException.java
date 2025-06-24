package com.plantstore.exception;

/**
 * Base exception class for the Plant Store application.
 * 
 * <p>This class provides a common base for all custom exceptions
 * in the application, ensuring consistent error handling.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class PlantStoreException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a new PlantStoreException with the specified message.
     * 
     * @param message the detail message
     */
    public PlantStoreException(String message) {
        super(message);
        this.errorCode = "PLANT_STORE_ERROR";
    }

    /**
     * Constructs a new PlantStoreException with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public PlantStoreException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PLANT_STORE_ERROR";
    }

    /**
     * Constructs a new PlantStoreException with the specified message and error code.
     * 
     * @param message the detail message
     * @param errorCode the error code
     */
    public PlantStoreException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new PlantStoreException with the specified message, cause, and error code.
     * 
     * @param message the detail message
     * @param cause the cause
     * @param errorCode the error code
     */
    public PlantStoreException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code for this exception.
     * 
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
} 