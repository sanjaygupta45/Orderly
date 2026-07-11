package com.orderflow.auth.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Issues HS256 JWTs. Validation lives in shared-security (gateway + services).
@Service
public class JwtService {

    @Value("${jwt.signingKey}")
    private String jwtSigningKey;

    @Value("${jwt.web.ttl-hour:24}")
    private long jwtExpirationHours;

    public String generateToken(String email, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);   // downstream services / gateway read this
        long expirationMillis = getExpirationTime();
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public long getExpirationTime() {
        return jwtExpirationHours * 60 * 60 * 1000;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSigningKey.getBytes());
    }
}
