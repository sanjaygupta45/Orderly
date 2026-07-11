package com.orderflow.gateway.filter;

import com.orderflow.shared.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// One global filter that: assigns/propagates a correlation id, validates the JWT on
// protected routes, forwards identity (X-User-Id / X-User-Role) downstream, and logs
// each request. Runs first so the identity header is available to the rate limiter.
@Component
@Slf4j
@RequiredArgsConstructor
public class GatewayGlobalFilter implements GlobalFilter, Ordered {

    private static final String CID = "X-Correlation-Id";
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        String incoming = request.getHeaders().getFirst(CID);
        final String cid = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;

        ServerHttpRequest mutated;
        if (isPublic(request.getMethod(), path)) {
            mutated = request.mutate().header(CID, cid).build();
        } else {
            // one parse verifies the token AND yields the claims
            String token = JwtService.stripBearer(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
            Claims claims = null;
            if (token != null) {
                try {
                    claims = jwtService.parse(token);
                } catch (Exception e) {
                    // invalid/expired -> handled below
                }
            }
            if (claims == null) {
                log.warn("401 {} {} cid={}", method, path, cid);
                return unauthorized(exchange, cid);
            }
            mutated = request.mutate()
                    .header(CID, cid)
                    .header("X-User-Id", String.valueOf(claims.get("userId")))
                    .header("X-User-Role", String.valueOf(claims.get("role")))
                    .build();
        }

        ServerWebExchange ex = exchange.mutate().request(mutated).build();
        ex.getResponse().getHeaders().set(CID, cid);
        long start = System.currentTimeMillis();
        return chain.filter(ex).doFinally(signal ->
                log.info("{} {} -> {} ({} ms) cid={}", method, path,
                        ex.getResponse().getStatusCode(), System.currentTimeMillis() - start, cid));
    }

    // Public: login/register, catalog browsing (GET products), actuator. Everything else needs a valid JWT.
    private boolean isPublic(HttpMethod httpMethod, String path) {
        if (path.startsWith("/actuator")) {
            return true;
        }
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            return true;
        }
        return HttpMethod.GET.equals(httpMethod) && path.startsWith("/api/v1/products");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String cid) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set(CID, cid);
        String body = "{\"success\":false,\"message\":\"Unauthorized\",\"correlationId\":\"" + cid + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
