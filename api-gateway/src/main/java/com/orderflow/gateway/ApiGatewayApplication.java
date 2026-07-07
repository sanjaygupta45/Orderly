package com.orderflow.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Scan the gateway's own package plus shared-security so JwtService/JwtProperties
// are picked up (shared-common is intentionally NOT pulled in - the gateway has no DB).
@SpringBootApplication(scanBasePackages = {"com.orderflow.gateway", "com.orderflow.shared.security"})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
