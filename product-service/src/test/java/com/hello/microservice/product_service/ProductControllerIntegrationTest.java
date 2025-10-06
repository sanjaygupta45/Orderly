package com.hello.microservice.product_service;

import com.hello.microservice.product_service.dto.ProductRequest;
import com.hello.microservice.product_service.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // Clean up database before each test if needed
    }

    @Test
    void createProduct_ShouldCreateProduct_WhenValidRequest() {
        // Arrange
        ProductRequest request = new ProductRequest("Integration Test Product", "Test Description", new BigDecimal("100.00"));

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                "/api/products/create", request, ProductResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals("Integration Test Product", response.getBody().name());
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Arrange - First create a product
        ProductRequest request = new ProductRequest("Test Product", "Test Desc", new BigDecimal("50.00"));
        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                "/api/products/create", request, ProductResponse.class
        );
        String productId = createResponse.getBody().id();

        // Act - Then retrieve it
        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
                "/api/products/get/" + productId, ProductResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().id());
    }
}