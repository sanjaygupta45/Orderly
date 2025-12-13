package com.orderly.order_service.config;

import org.springframework.context.annotation.Configuration;

/**
 * Resilience4j configuration for circuit breakers.
 * Configuration is managed via application.properties
 */
@Configuration
public class Resilience4jConfig {
    // Configuration handled via application.properties
    // resilience4j.circuitbreaker.instances.*
    // resilience4j.retry.instances.*
    // resilience4j.timelimiter.instances.*
}

