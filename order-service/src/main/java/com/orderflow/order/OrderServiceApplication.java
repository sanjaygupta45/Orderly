package com.orderflow.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Scan/entity/repository packages are widened to com.orderflow so the reusable
// shared-common beans, entities (ProcessedMessage, OutboxEvent) and repositories
// are picked up alongside this service's own.
@SpringBootApplication(scanBasePackages = "com.orderflow")
@EntityScan("com.orderflow")
@EnableJpaRepositories("com.orderflow")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
