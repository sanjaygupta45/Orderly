package com.orderflow.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

// Verifies and reads JWTs issued by auth-service. Uses the SAME HS256 secret so
// any service can validate a token locally, without calling auth on every request.
@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    private volatile SecretKey signingKey;

    public boolean isValid(String token) {
        try {
            parse(token);   // verifies signature + expiry, throws if bad
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public String extractRole(String token) {
        return parse(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        Object userId = parse(token).get("userId");
        return userId == null ? null : Long.valueOf(userId.toString());
    }

    private SecretKey key() {
        if (signingKey == null) {
            // Match auth-service byte-for-byte: raw bytes of the configured key string.
            signingKey = Keys.hmacShaKeyFor(properties.getSigningKey().getBytes());
        }
        return signingKey;
    }
}
