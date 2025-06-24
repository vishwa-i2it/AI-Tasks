# Database Query Caching Layer

A comprehensive caching layer for database queries implemented in Java with Redis integration, cache invalidation strategies, and TTL management.

## Project Structure

```
src/main/java/com/cache/
├── core/           # Core interfaces and exceptions
│   ├── Cache.java
│   ├── CacheException.java
│   └── CacheConfig.java
├── impl/           # Cache implementations
│   └── InMemoryCache.java
└── example/        # Usage examples
    └── CacheExample.java
```

## Step 1: Cache Interface Design

### Overview
The cache interface provides a flexible, type-safe contract for all cache implementations. It follows the Strategy pattern, allowing different cache backends (Redis, in-memory, etc.) to be used interchangeably.

### Key Features

#### 1. Core Operations
- **get(K key)**: Retrieves a value from cache with Optional return type
- **set(K key, V value)**: Stores a value with default TTL
- **set(K key, V value, long ttl, TimeUnit timeUnit)**: Stores a value with custom TTL
- **delete(K key)**: Removes a value from cache
- **exists(K key)**: Checks if a key exists

#### 2. Bulk Operations
- **getMultiple(Collection<K> keys)**: Retrieves multiple values efficiently
- **setMultiple(Map<K, V> entries)**: Stores multiple values with default TTL
- **setMultiple(Map<K, V> entries, long ttl, TimeUnit timeUnit)**: Stores multiple values with custom TTL
- **deleteMultiple(Collection<K> keys)**: Removes multiple keys

#### 3. Utility Operations
- **clear()**: Removes all entries from cache
- **size()**: Returns current cache size
- **isHealthy()**: Checks cache health status
- **close()**: Properly closes cache and releases resources

### Configuration
The `CacheConfig` class provides flexible configuration options:
- Default TTL settings
- Retry policies
- Connection and operation timeouts
- Metrics and compression settings
- Size limits for keys and values

### Error Handling
Custom `CacheException` with categorized error types:
- `CONNECTION_FAILED`: Network or connection issues
- `OPERATION_FAILED`: General operation failures
- `SERIALIZATION_ERROR`: Data serialization issues
- `INVALID_KEY/VALUE`: Input validation errors
- `TIMEOUT`: Operation timeouts
- `MEMORY_FULL`: Cache capacity issues

### Implementation Example

```java
// Create configuration
CacheConfig config = CacheConfig.builder()
    .defaultTtl(Duration.ofMinutes(30))
    .maxRetries(3)
    .connectionTimeout(Duration.ofSeconds(5))
    .build();

// Create cache instance
Cache<String, String> cache = new InMemoryCache<>(config);

// Basic operations
cache.set("key", "value");
Optional<String> value = cache.get("key");
boolean deleted = cache.delete("key");

// Bulk operations
Map<String, String> entries = Map.of("k1", "v1", "k2", "v2");
cache.setMultiple(entries);
Map<String, String> retrieved = cache.getMultiple(Arrays.asList("k1", "k2"));
```

## Potential Issues and Solutions

### 1. Memory Management
**Issue**: In-memory cache can consume excessive memory
**Solution**: 
- Implement LRU eviction policy
- Add memory usage monitoring
- Set maximum cache size limits

### 2. Thread Safety
**Issue**: Concurrent access can cause data corruption
**Solution**: 
- Use `ConcurrentHashMap` for thread-safe operations
- Implement proper synchronization for complex operations
- Add atomic operations where needed

### 3. Serialization
**Issue**: Complex objects need proper serialization
**Solution**: 
- Use Jackson for JSON serialization
- Implement custom serializers for specific types
- Add compression for large objects

### 4. Error Recovery
**Issue**: Cache failures can impact application performance
**Solution**: 
- Implement circuit breaker pattern
- Add fallback mechanisms
- Graceful degradation when cache is unavailable

## Suggested Improvements

### 1. Enhanced Monitoring
```java
// Add metrics collection
public interface CacheMetrics {
    void recordHit(String key);
    void recordMiss(String key);
    void recordError(String operation, String error);
    Map<String, Long> getStatistics();
}
```

### 2. Cache Patterns
```java
// Implement common caching patterns
public interface CachePatterns {
    <T> T getOrLoad(K key, Supplier<T> loader);
    <T> CompletableFuture<T> getOrLoadAsync(K key, Supplier<CompletableFuture<T>> loader);
    void invalidatePattern(String pattern);
}
```

### 3. Distributed Cache Support
```java
// Add distributed cache capabilities
public interface DistributedCache<K, V> extends Cache<K, V> {
    void replicate(K key, V value);
    void syncWithOtherNodes();
    List<String> getConnectedNodes();
}
```

### 4. Advanced TTL Management
```java
// Implement sliding TTL and access-based expiration
public interface AdvancedTTL {
    void setWithSlidingTTL(K key, V value, Duration ttl);
    void extendTTL(K key, Duration additionalTtl);
    void setAccessBasedExpiration(K key, V value, int maxAccesses);
}
```

## Next Steps

1. **Step 2**: Implement Redis integration with connection pooling and failover
2. **Step 3**: Add cache invalidation strategies and advanced TTL management
3. **Step 4**: Implement monitoring, metrics, and performance optimization

## Building and Running

```bash
# Build the project
mvn clean compile

# Run the example
mvn exec:java -Dexec.mainClass="com.cache.example.CacheExample"

# Run tests
mvn test
```

## Dependencies

- Java 11+
- Maven 3.6+
- Redis (for Step 2)
- Jackson (for JSON serialization)
- SLF4J (for logging)
- JUnit 5 (for testing)

## 📝 Prompt Examples

### 1. Basic Prompt
```
Implement a caching layer for database queries in Java.
```

### 2. Chain-of-Thought Prompt
```
I need to add a caching layer to my Java application. Let me break this down:
Step 1: Design a Cache interface with get/set/delete methods.
Step 2: Implement an in-memory cache.
Step 3: Add support for TTL and cache invalidation.
Step 4: Integrate the cache with database queries.
For each step, please:
- Explain the approach
- Write the code
- Suggest improvements
Start with Step 1.
```

### 3. Context-Rich Prompt
```
Context: I am building a Java application that frequently queries a relational database. To improve performance, I want to add a caching layer with support for in-memory and Redis backends, TTL, and cache invalidation.

Current Architecture: Java 11+, Spring Boot, JPA, MySQL. No caching currently implemented.

Technology Stack: Java 11+, Spring Boot, JPA, Redis (for distributed cache)

Constraints: The cache must be thread-safe, support custom TTL, and allow for easy switching between in-memory and Redis implementations. Performance and reliability are critical.

Task: Design and implement a flexible caching layer with a common interface, in-memory and Redis implementations, and integration with database queries.

Expected Output: Java code for the cache interface, in-memory and Redis implementations, and example usage in a service class.

Quality Criteria: Code should be modular, well-documented, thread-safe, and tested for both cache hits and misses.
``` 