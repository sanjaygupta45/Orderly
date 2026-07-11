package com.orderflow.shared.common.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Names each service's Swagger UI page after the service instead of springdoc's
// generic default. One shared config replaces the identical per-service copies;
// only active when springdoc is on the classpath.
@Configuration
@ConditionalOnClass(OpenAPI.class)
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(@Value("${spring.application.name}") String serviceName) {
        return new OpenAPI().info(new Info().title("OrderFlow - " + serviceName).version("1.0.0"));
    }
}
