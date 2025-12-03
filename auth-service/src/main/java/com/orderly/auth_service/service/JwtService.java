package com.orderly.auth_service.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.function.Function;
import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateToken(UserDetails userDetails, Collection<String> roles);

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> resolver);

    boolean isTokenValid(String token, UserDetails userDetails);
}
