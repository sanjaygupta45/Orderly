package com.orderflow.shared.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Bound from `jwt.signingKey` - the shared HS256 secret used to verify tokens.
// In real deployments this is supplied via an environment variable, never hardcoded.
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private String signingKey;
}
