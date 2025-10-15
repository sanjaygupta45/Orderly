package com.orderly.product_service;


import com.hello.microservice.product_service.service.ProductService;
import com.orderly.product_service.controllers.ProductController;
import com.orderly.product_service.dto.ProductRequest;
import com.orderly.product_service.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        // Arrange
        ProductRequest request = new ProductRequest("Laptop", "Gaming laptop", new BigDecimal("999.99"));
        ProductResponse expectedResponse = new ProductResponse(
                "507f1f77bcf86cd799439011", "Laptop", "Gaming laptop", new BigDecimal("999.99")
        );

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("507f1f77bcf86cd799439011", response.getBody().id());
        verify(productService, times(1)).createProduct(request);
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenValidId() {
        // Arrange
        String productId = "507f1f77bcf86cd799439011";
        ProductResponse expectedResponse = new ProductResponse(
                productId, "Laptop", "Gaming laptop", new BigDecimal("999.99")
        );

        when(productService.getProductById(productId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ProductResponse> response = productController.getProductById(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().id());
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void getAllProducts_ShouldReturnProductsList() {
        // Arrange
        List<ProductResponse> expectedResponses = List.of(
                new ProductResponse("1", "Laptop", "Gaming laptop", new BigDecimal("999.99")),
                new ProductResponse("2", "Mouse", "Wireless mouse", new BigDecimal("29.99"))
        );

        when(productService.getAllProducts(any())).thenReturn(expectedResponses);

        // Act
        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts(null, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(productService, times(1)).getAllProducts(any());
    }
}