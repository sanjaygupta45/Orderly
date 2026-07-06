package com.orderflow.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Scan widened to com.orderflow so shared-common beans/entities/repositories load too.
@SpringBootApplication(scanBasePackages = "com.orderflow")
@EntityScan("com.orderflow")
@EnableJpaRepositories("com.orderflow")
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
