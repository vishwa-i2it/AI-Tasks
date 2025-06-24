package com.cache.example;

import com.cache.core.Cache;
import com.cache.core.CacheConfig;
import com.cache.core.CacheException;
import com.cache.impl.InMemoryCache;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Example demonstrating the usage of the cache interface.
 */
public class CacheExample {
    
    public static void main(String[] args) {
        // Create cache with custom configuration
        CacheConfig config = CacheConfig.builder()
                .defaultTtl(Duration.ofMinutes(5))
                .maxRetries(3)
                .connectionTimeout(Duration.ofSeconds(2))
                .operationTimeout(Duration.ofSeconds(5))
                .enableMetrics(true)
                .build();
        
        // Create in-memory cache instance
        Cache<String, String> cache = new InMemoryCache<>(config);
        
        try {
            // Basic operations
            System.out.println("=== Basic Cache Operations ===");
            
            // Set a value
            cache.set("user:1", "John Doe");
            System.out.println("Set user:1 = John Doe");
            
            // Get a value
            Optional<String> user = cache.get("user:1");
            if (user.isPresent()) {
                System.out.println("Retrieved user:1 = " + user.get());
            }
            
            // Check if key exists
            boolean exists = cache.exists("user:1");
            System.out.println("Key 'user:1' exists: " + exists);
            
            // Set with custom TTL
            cache.set("temp:data", "Temporary data", 10, TimeUnit.SECONDS);
            System.out.println("Set temp:data with 10 second TTL");
            
            // Bulk operations
            System.out.println("\n=== Bulk Operations ===");
            
            Map<String, String> users = Map.of(
                "user:2", "Jane Smith",
                "user:3", "Bob Johnson",
                "user:4", "Alice Brown"
            );
            
            cache.setMultiple(users);
            System.out.println("Set multiple users");
            
            Map<String, String> retrievedUsers = cache.getMultiple(Arrays.asList("user:1", "user:2", "user:3"));
            System.out.println("Retrieved users: " + retrievedUsers);
            
            // Delete operations
            System.out.println("\n=== Delete Operations ===");
            
            boolean deleted = cache.delete("user:1");
            System.out.println("Deleted user:1: " + deleted);
            
            int deletedCount = cache.deleteMultiple(Arrays.asList("user:2", "user:3", "user:4"));
            System.out.println("Deleted " + deletedCount + " users");
            
            // Cache statistics
            System.out.println("\n=== Cache Statistics ===");
            System.out.println("Cache size: " + cache.size());
            System.out.println("Cache healthy: " + cache.isHealthy());
            
            // Clear cache
            cache.clear();
            System.out.println("Cache cleared");
            System.out.println("Cache size after clear: " + cache.size());
            
        } catch (CacheException e) {
            System.err.println("Cache error: " + e.getMessage());
            System.err.println("Error type: " + e.getErrorType());
        } finally {
            try {
                cache.close();
                System.out.println("Cache closed");
            } catch (CacheException e) {
                System.err.println("Error closing cache: " + e.getMessage());
            }
        }
    }
} 