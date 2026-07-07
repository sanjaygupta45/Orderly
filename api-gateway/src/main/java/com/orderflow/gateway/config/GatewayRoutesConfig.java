package com.orderflow.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Explicit routes to each service. URIs are env-overridable so the same build runs
// locally (localhost) and in Docker (container names). Every route is rate limited.
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder,
                               RedisRateLimiter rateLimiter,
                               KeyResolver keyResolver,
                               @Value("${orderflow.routes.auth-uri}") String authUri,
                               @Value("${orderflow.routes.product-uri}") String productUri,
                               @Value("${orderflow.routes.order-uri}") String orderUri,
                               @Value("${orderflow.routes.inventory-uri}") String inventoryUri,
                               @Value("${orderflow.routes.payment-uri}") String paymentUri,
                               @Value("${orderflow.routes.notification-uri}") String notificationUri) {

        return builder.routes()
                .route("auth", r -> r.path("/api/auth/**")
                        .filters(f -> rateLimited(f, rateLimiter, keyResolver)).uri(authUri))
                .route("products", r -> r.path("/api/v1/products/**")
                        .filters(f -> rateLimited(f, rateLimiter, keyResolver)).uri(productUri))
                .route("orders", r -> r.path("/api/v1/orders/**")
                        .filters(f -> rateLimited(f, rateLimiter, keyResolver)).uri(orderUri))
                .route("inventory", r -> r.path("/api/v1/inventory/**")
                        .filters(f -> rateLimited(f, rateLimiter, keyResolver)).uri(inventoryUri))
                .route("payments", r -> r.path("/api/v1/payments/**")
                        .filters(f -> rateLimited(f, rateLimiter, keyResolver)).uri(paymentUri))
                .route("notifications", r -> r.path("/api/v1/notifications/**")
                        .filters(f -> rateLimited(f, rateLimiter, keyResolver)).uri(notificationUri))
                .build();
    }

    private GatewayFilterSpec rateLimited(GatewayFilterSpec f, RedisRateLimiter limiter, KeyResolver resolver) {
        return f.requestRateLimiter(c -> c.setRateLimiter(limiter).setKeyResolver(resolver));
    }
}
