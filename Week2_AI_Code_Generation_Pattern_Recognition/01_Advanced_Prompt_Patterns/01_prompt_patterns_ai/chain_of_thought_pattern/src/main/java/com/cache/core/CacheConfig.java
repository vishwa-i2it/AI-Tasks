package com.cache.core;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for cache settings.
 * Provides builder pattern for easy configuration setup.
 */
public class CacheConfig {
    
    private final Duration defaultTtl;
    private final int maxRetries;
    private final Duration connectionTimeout;
    private final Duration operationTimeout;
    private final boolean enableMetrics;
    private final boolean enableCompression;
    private final int maxKeySize;
    private final int maxValueSize;
    
    private CacheConfig(Builder builder) {
        this.defaultTtl = builder.defaultTtl;
        this.maxRetries = builder.maxRetries;
        this.connectionTimeout = builder.connectionTimeout;
        this.operationTimeout = builder.operationTimeout;
        this.enableMetrics = builder.enableMetrics;
        this.enableCompression = builder.enableCompression;
        this.maxKeySize = builder.maxKeySize;
        this.maxValueSize = builder.maxValueSize;
    }
    
    /**
     * Gets the default time-to-live for cache entries.
     * 
     * @return default TTL duration
     */
    public Duration getDefaultTtl() {
        return defaultTtl;
    }
    
    /**
     * Gets the maximum number of retry attempts for failed operations.
     * 
     * @return maximum retry count
     */
    public int getMaxRetries() {
        return maxRetries;
    }
    
    /**
     * Gets the connection timeout duration.
     * 
     * @return connection timeout
     */
    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }
    
    /**
     * Gets the operation timeout duration.
     * 
     * @return operation timeout
     */
    public Duration getOperationTimeout() {
        return operationTimeout;
    }
    
    /**
     * Checks if metrics collection is enabled.
     * 
     * @return true if metrics are enabled
     */
    public boolean isMetricsEnabled() {
        return enableMetrics;
    }
    
    /**
     * Checks if compression is enabled.
     * 
     * @return true if compression is enabled
     */
    public boolean isCompressionEnabled() {
        return enableCompression;
    }
    
    /**
     * Gets the maximum allowed key size in bytes.
     * 
     * @return maximum key size
     */
    public int getMaxKeySize() {
        return maxKeySize;
    }
    
    /**
     * Gets the maximum allowed value size in bytes.
     * 
     * @return maximum value size
     */
    public int getMaxValueSize() {
        return maxValueSize;
    }
    
    /**
     * Builder class for CacheConfig.
     */
    public static class Builder {
        private Duration defaultTtl = Duration.ofMinutes(30);
        private int maxRetries = 3;
        private Duration connectionTimeout = Duration.ofSeconds(5);
        private Duration operationTimeout = Duration.ofSeconds(10);
        private boolean enableMetrics = true;
        private boolean enableCompression = false;
        private int maxKeySize = 1024; // 1KB
        private int maxValueSize = 1024 * 1024; // 1MB
        
        /**
         * Sets the default TTL for cache entries.
         * 
         * @param ttl the default TTL duration
         * @return this builder
         */
        public Builder defaultTtl(Duration ttl) {
            this.defaultTtl = ttl;
            return this;
        }
        
        /**
         * Sets the maximum number of retry attempts.
         * 
         * @param maxRetries the maximum retry count
         * @return this builder
         */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
        
        /**
         * Sets the connection timeout.
         * 
         * @param timeout the connection timeout duration
         * @return this builder
         */
        public Builder connectionTimeout(Duration timeout) {
            this.connectionTimeout = timeout;
            return this;
        }
        
        /**
         * Sets the operation timeout.
         * 
         * @param timeout the operation timeout duration
         * @return this builder
         */
        public Builder operationTimeout(Duration timeout) {
            this.operationTimeout = timeout;
            return this;
        }
        
        /**
         * Enables or disables metrics collection.
         * 
         * @param enable true to enable metrics
         * @return this builder
         */
        public Builder enableMetrics(boolean enable) {
            this.enableMetrics = enable;
            return this;
        }
        
        /**
         * Enables or disables compression.
         * 
         * @param enable true to enable compression
         * @return this builder
         */
        public Builder enableCompression(boolean enable) {
            this.enableCompression = enable;
            return this;
        }
        
        /**
         * Sets the maximum key size.
         * 
         * @param maxKeySize maximum key size in bytes
         * @return this builder
         */
        public Builder maxKeySize(int maxKeySize) {
            this.maxKeySize = maxKeySize;
            return this;
        }
        
        /**
         * Sets the maximum value size.
         * 
         * @param maxValueSize maximum value size in bytes
         * @return this builder
         */
        public Builder maxValueSize(int maxValueSize) {
            this.maxValueSize = maxValueSize;
            return this;
        }
        
        /**
         * Builds the CacheConfig instance.
         * 
         * @return the configured CacheConfig
         */
        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }
    
    /**
     * Creates a new builder instance.
     * 
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a default configuration.
     * 
     * @return default CacheConfig
     */
    public static CacheConfig defaultConfig() {
        return builder().build();
    }
} 