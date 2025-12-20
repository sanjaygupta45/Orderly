package com.orderly.order_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.CompletableFuture;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryClient {

    @GetMapping("/inventory")
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "checkStockFallback")
    @Retry(name = "inventory-service")
    @TimeLimiter(name = "inventory-service")
    CompletableFuture<Boolean> checkStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default CompletableFuture<Boolean> checkStockFallback(String skuCode, Integer quantity, Exception ex) {

        return CompletableFuture.completedFuture(true);
    }
}
