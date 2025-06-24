package com.cache.core;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Core cache interface defining the contract for all cache implementations.
 * Supports basic CRUD operations, bulk operations, and TTL management.
 * 
 * @param <K> the type of cache keys
 * @param <V> the type of cache values
 */
public interface Cache<K, V> {
    
    /**
     * Retrieves a value from the cache.
     * 
     * @param key the cache key
     * @return Optional containing the value if found, empty otherwise
     * @throws CacheException if cache operation fails
     */
    Optional<V> get(K key) throws CacheException;
    
    /**
     * Stores a value in the cache with default TTL.
     * 
     * @param key the cache key
     * @param value the value to store
     * @throws CacheException if cache operation fails
     */
    void set(K key, V value) throws CacheException;
    
    /**
     * Stores a value in the cache with custom TTL.
     * 
     * @param key the cache key
     * @param value the value to store
     * @param ttl time-to-live duration
     * @param timeUnit time unit for TTL
     * @throws CacheException if cache operation fails
     */
    void set(K key, V value, long ttl, TimeUnit timeUnit) throws CacheException;
    
    /**
     * Removes a value from the cache.
     * 
     * @param key the cache key to remove
     * @return true if the key was present and removed, false otherwise
     * @throws CacheException if cache operation fails
     */
    boolean delete(K key) throws CacheException;
    
    /**
     * Checks if a key exists in the cache.
     * 
     * @param key the cache key to check
     * @return true if the key exists, false otherwise
     * @throws CacheException if cache operation fails
     */
    boolean exists(K key) throws CacheException;
    
    /**
     * Retrieves multiple values from the cache.
     * 
     * @param keys collection of cache keys
     * @return map of key-value pairs for existing keys
     * @throws CacheException if cache operation fails
     */
    Map<K, V> getMultiple(Collection<K> keys) throws CacheException;
    
    /**
     * Stores multiple values in the cache with default TTL.
     * 
     * @param entries map of key-value pairs to store
     * @throws CacheException if cache operation fails
     */
    void setMultiple(Map<K, V> entries) throws CacheException;
    
    /**
     * Stores multiple values in the cache with custom TTL.
     * 
     * @param entries map of key-value pairs to store
     * @param ttl time-to-live duration
     * @param timeUnit time unit for TTL
     * @throws CacheException if cache operation fails
     */
    void setMultiple(Map<K, V> entries, long ttl, TimeUnit timeUnit) throws CacheException;
    
    /**
     * Removes multiple keys from the cache.
     * 
     * @param keys collection of cache keys to remove
     * @return number of keys that were present and removed
     * @throws CacheException if cache operation fails
     */
    int deleteMultiple(Collection<K> keys) throws CacheException;
    
    /**
     * Clears all entries from the cache.
     * 
     * @throws CacheException if cache operation fails
     */
    void clear() throws CacheException;
    
    /**
     * Gets the current size of the cache.
     * 
     * @return number of entries in the cache
     * @throws CacheException if cache operation fails
     */
    long size() throws CacheException;
    
    /**
     * Checks if the cache is healthy and operational.
     * 
     * @return true if cache is healthy, false otherwise
     */
    boolean isHealthy();
    
    /**
     * Closes the cache and releases resources.
     * 
     * @throws CacheException if cache operation fails
     */
    void close() throws CacheException;
} 