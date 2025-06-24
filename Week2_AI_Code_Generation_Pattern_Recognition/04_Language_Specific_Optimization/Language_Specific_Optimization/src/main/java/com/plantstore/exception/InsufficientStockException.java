package com.plantstore.exception;

/**
 * Exception thrown when there is insufficient stock to fulfill a request.
 * 
 * <p>This exception is typically thrown when trying to order more items
 * than are available in stock.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class InsufficientStockException extends PlantStoreException {

    private final Long plantId;
    private final String plantName;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;

    /**
     * Constructs a new InsufficientStockException with the specified details.
     * 
     * @param plantId the ID of the plant
     * @param plantName the name of the plant
     * @param requestedQuantity the quantity requested
     * @param availableQuantity the quantity available in stock
     */
    public InsufficientStockException(Long plantId, String plantName, 
                                    Integer requestedQuantity, Integer availableQuantity) {
        super(String.format("Insufficient stock for plant '%s'. Requested: %d, Available: %d", 
                           plantName, requestedQuantity, availableQuantity));
        this.plantId = plantId;
        this.plantName = plantName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Constructs a new InsufficientStockException with a custom message.
     * 
     * @param message the detail message
     * @param plantId the ID of the plant
     * @param plantName the name of the plant
     * @param requestedQuantity the quantity requested
     * @param availableQuantity the quantity available in stock
     */
    public InsufficientStockException(String message, Long plantId, String plantName, 
                                    Integer requestedQuantity, Integer availableQuantity) {
        super(message);
        this.plantId = plantId;
        this.plantName = plantName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Gets the ID of the plant that has insufficient stock.
     * 
     * @return the plant ID
     */
    public Long getPlantId() {
        return plantId;
    }

    /**
     * Gets the name of the plant that has insufficient stock.
     * 
     * @return the plant name
     */
    public String getPlantName() {
        return plantName;
    }

    /**
     * Gets the quantity that was requested.
     * 
     * @return the requested quantity
     */
    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    /**
     * Gets the quantity that is available in stock.
     * 
     * @return the available quantity
     */
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
} 