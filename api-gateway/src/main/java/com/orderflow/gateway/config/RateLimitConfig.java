package com.orderflow.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Optional;

// Redis-backed token-bucket rate limiter.
@Configuration
public class RateLimitConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter(
            @Value("${orderflow.ratelimit.replenish-rate}") int replenishRate,
            @Value("${orderflow.ratelimit.burst-capacity}") int burstCapacity) {
        return new RedisRateLimiter(replenishRate, burstCapacity);
    }

    // Limit per authenticated user (X-User-Id, set by the global filter),
    // falling back to client IP for anonymous/public traffic.
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                return Mono.just(userId);
            }
            String ip = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                    .map(addr -> addr.getAddress().getHostAddress())
                    .orElse("anonymous");
            return Mono.just(ip);
        };
    }
}
