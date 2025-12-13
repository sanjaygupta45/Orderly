package com.orderly.order_service.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global Feign configuration for all clients.
 * Handles request/response logging and error handling.
 */
@Configuration
public class FeignConfig {

    /**
     * Set Feign logging level to FULL for detailed debugging.
     * Logs request/response headers and body.
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}

