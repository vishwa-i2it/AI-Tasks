package com.cache.impl;

import com.cache.core.Cache;
import com.cache.core.CacheConfig;
import com.cache.core.CacheException;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Simple in-memory cache implementation for demonstration purposes.
 * This implementation is not suitable for production use in distributed environments.
 * 
 * @param <K> the type of cache keys
 * @param <V> the type of cache values
 */
public class InMemoryCache<K, V> implements Cache<K, V> {
    
    private final Map<K, CacheEntry<V>> cache;
    private final CacheConfig config;
    private volatile boolean isClosed = false;
    
    /**
     * Internal class to store cache entries with TTL information.
     */
    private static class CacheEntry<V> {
        private final V value;
        private final Instant expiryTime;
        
        CacheEntry(V value, Duration ttl) {
            this.value = value;
            this.expiryTime = Instant.now().plus(ttl);
        }
        
        boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
        
        V getValue() {
            return value;
        }
    }
    
    /**
     * Creates an in-memory cache with default configuration.
     */
    public InMemoryCache() {
        this(CacheConfig.defaultConfig());
    }
    
    /**
     * Creates an in-memory cache with custom configuration.
     * 
     * @param config the cache configuration
     */
    public InMemoryCache(CacheConfig config) {
        this.cache = new ConcurrentHashMap<>();
        this.config = config;
    }
    
    @Override
    public Optional<V> get(K key) throws CacheException {
        checkNotClosed();
        validateKey(key);
        
        try {
            CacheEntry<V> entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                if (entry != null && entry.isExpired()) {
                    cache.remove(key);
                }
                return Optional.empty();
            }
            return Optional.of(entry.getValue());
        } catch (Exception e) {
            throw new CacheException("Failed to get value for key: " + key, e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public void set(K key, V value) throws CacheException {
        set(key, value, config.getDefaultTtl().toMillis(), TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void set(K key, V value, long ttl, TimeUnit timeUnit) throws CacheException {
        checkNotClosed();
        validateKey(key);
        validateValue(value);
        
        try {
            Duration ttlDuration = Duration.ofMillis(timeUnit.toMillis(ttl));
            cache.put(key, new CacheEntry<>(value, ttlDuration));
        } catch (Exception e) {
            throw new CacheException("Failed to set value for key: " + key, e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public boolean delete(K key) throws CacheException {
        checkNotClosed();
        validateKey(key);
        
        try {
            return cache.remove(key) != null;
        } catch (Exception e) {
            throw new CacheException("Failed to delete key: " + key, e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public boolean exists(K key) throws CacheException {
        checkNotClosed();
        validateKey(key);
        
        try {
            CacheEntry<V> entry = cache.get(key);
            if (entry == null) {
                return false;
            }
            if (entry.isExpired()) {
                cache.remove(key);
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new CacheException("Failed to check existence for key: " + key, e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public Map<K, V> getMultiple(Collection<K> keys) throws CacheException {
        checkNotClosed();
        if (keys == null || keys.isEmpty()) {
            return Map.of();
        }
        
        try {
            return keys.stream()
                    .filter(key -> {
                        try {
                            return exists(key);
                        } catch (CacheException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toMap(
                            key -> key,
                            key -> get(key).orElse(null)
                    ));
        } catch (Exception e) {
            throw new CacheException("Failed to get multiple values", e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public void setMultiple(Map<K, V> entries) throws CacheException {
        setMultiple(entries, config.getDefaultTtl().toMillis(), TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void setMultiple(Map<K, V> entries, long ttl, TimeUnit timeUnit) throws CacheException {
        checkNotClosed();
        if (entries == null || entries.isEmpty()) {
            return;
        }
        
        try {
            Duration ttlDuration = Duration.ofMillis(timeUnit.toMillis(ttl));
            entries.forEach((key, value) -> {
                try {
                    validateKey(key);
                    validateValue(value);
                    cache.put(key, new CacheEntry<>(value, ttlDuration));
                } catch (CacheException e) {
                    // Log error but continue with other entries
                    System.err.println("Failed to set entry for key " + key + ": " + e.getMessage());
                }
            });
        } catch (Exception e) {
            throw new CacheException("Failed to set multiple values", e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public int deleteMultiple(Collection<K> keys) throws CacheException {
        checkNotClosed();
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        
        try {
            int deletedCount = 0;
            for (K key : keys) {
                if (delete(key)) {
                    deletedCount++;
                }
            }
            return deletedCount;
        } catch (Exception e) {
            throw new CacheException("Failed to delete multiple keys", e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public void clear() throws CacheException {
        checkNotClosed();
        
        try {
            cache.clear();
        } catch (Exception e) {
            throw new CacheException("Failed to clear cache", e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public long size() throws CacheException {
        checkNotClosed();
        
        try {
            // Remove expired entries and return actual size
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            return cache.size();
        } catch (Exception e) {
            throw new CacheException("Failed to get cache size", e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    @Override
    public boolean isHealthy() {
        return !isClosed;
    }
    
    @Override
    public void close() throws CacheException {
        isClosed = true;
        try {
            cache.clear();
        } catch (Exception e) {
            throw new CacheException("Failed to close cache", e, 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    private void checkNotClosed() throws CacheException {
        if (isClosed) {
            throw new CacheException("Cache is closed", 
                                   CacheException.CacheErrorType.OPERATION_FAILED);
        }
    }
    
    private void validateKey(K key) throws CacheException {
        if (key == null) {
            throw new CacheException("Cache key cannot be null", 
                                   CacheException.CacheErrorType.INVALID_KEY);
        }
        
        String keyString = key.toString();
        if (keyString.length() > config.getMaxKeySize()) {
            throw new CacheException("Cache key size exceeds maximum allowed size", 
                                   CacheException.CacheErrorType.INVALID_KEY);
        }
    }
    
    private void validateValue(V value) throws CacheException {
        if (value == null) {
            throw new CacheException("Cache value cannot be null", 
                                   CacheException.CacheErrorType.INVALID_VALUE);
        }
        
        // Simple size check - in production, you'd want more sophisticated serialization
        String valueString = value.toString();
        if (valueString.length() > config.getMaxValueSize()) {
            throw new CacheException("Cache value size exceeds maximum allowed size", 
                                   CacheException.CacheErrorType.INVALID_VALUE);
        }
    }
} 