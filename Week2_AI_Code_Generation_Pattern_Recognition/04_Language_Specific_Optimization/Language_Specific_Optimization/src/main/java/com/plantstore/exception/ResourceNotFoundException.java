package com.plantstore.exception;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * <p>This exception is typically thrown when trying to retrieve,
 * update, or delete a resource that does not exist in the system.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ResourceNotFoundException extends PlantStoreException {

    private final String resourceType;
    private final String resourceId;

    /**
     * Constructs a new ResourceNotFoundException with the specified resource type and ID.
     * 
     * @param resourceType the type of resource that was not found
     * @param resourceId the ID of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s with id %s not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified resource type and ID.
     * 
     * @param resourceType the type of resource that was not found
     * @param resourceId the ID of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, Long resourceId) {
        this(resourceType, String.valueOf(resourceId));
    }

    /**
     * Constructs a new ResourceNotFoundException with a custom message.
     * 
     * @param message the detail message
     * @param resourceType the type of resource that was not found
     * @param resourceId the ID of the resource that was not found
     */
    public ResourceNotFoundException(String message, String resourceType, String resourceId) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Gets the type of resource that was not found.
     * 
     * @return the resource type
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Gets the ID of the resource that was not found.
     * 
     * @return the resource ID
     */
    public String getResourceId() {
        return resourceId;
    }
} 