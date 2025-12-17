package com.orderly.order_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.CompletableFuture;

/**
 * Feign client for communicating with Inventory Service.
 * Handles stock validation for orders.
 */
@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryClient {

    /**
     * Check if product is in stock.
     *
     * @param skuCode  Product SKU code
     * @param quantity Quantity required
     * @return true if in stock, false otherwise
     */
    @GetMapping("/inventory")
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "checkStockFallback")
    @Retry(name = "inventory-service")
    @TimeLimiter(name = "inventory-service")
    CompletableFuture<Boolean> checkStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    /**
     * Fallback method when inventory service is down or circuit breaker opens.
     * Returns true to allow order placement (fail-open approach).
     * In production, you might want to queue orders for later processing.
     */
    default CompletableFuture<Boolean> checkStockFallback(String skuCode, Integer quantity, Exception ex) {
        // Fallback: Allow order placement but log the issue
        // In production, consider: returning false, queueing to retry, or alerting ops
        return CompletableFuture.completedFuture(true);
    }
}
