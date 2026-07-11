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

    // Verifies signature + expiry and returns the claims; throws JwtException if invalid.
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // "Bearer xyz" -> "xyz"; null if the header is missing or not a bearer token.
    public static String stripBearer(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private SecretKey key() {
        if (signingKey == null) {
            // Match auth-service byte-for-byte: raw bytes of the configured key string.
            signingKey = Keys.hmacShaKeyFor(properties.getSigningKey().getBytes());
        }
        return signingKey;
    }
}
