package com.orderflow.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Scan the product package plus shared-security (for JwtService). shared-common's
// JPA beans are intentionally not scanned - product only uses its DTO classes.
@SpringBootApplication(scanBasePackages = {"com.orderflow.product", "com.orderflow.shared.security"})
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
