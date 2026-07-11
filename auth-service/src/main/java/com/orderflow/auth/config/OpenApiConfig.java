package com.orderflow.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Names the Swagger UI page after this service instead of springdoc's generic default.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(@Value("${spring.application.name}") String serviceName) {
        return new OpenAPI().info(new Info().title("OrderFlow - " + serviceName).version("1.0.0"));
    }
}
