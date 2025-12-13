package com.orderly.product_service.controllers;

import com.orderly.product_service.service.ProductService;
import com.orderly.product_service.dto.ProductFilterRequest;
import com.orderly.product_service.dto.ProductRequest;
import com.orderly.product_service.dto.ProductResponse;
import com.orderly.product_service.exception.ApiError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Product Controller - Handles product catalog operations
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ApiError> createProduct(@RequestBody ProductRequest productRequest) {
        try {
            log.info("Creating product: {}", productRequest.name());
            ProductResponse productResponse = productService.createProduct(productRequest);

            ApiError response = ApiError.builder()
                    .success(true)
                    .message("Product created successfully")
                    .status(HttpStatus.CREATED.value())
                    .timestamp(LocalDateTime.now())
                    .details(productResponse)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            ApiError response = ApiError.builder()
                    .success(false)
                    .message("Error creating product: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/get/all", produces = "application/json")
    public ResponseEntity<ApiError> getAllProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            log.info("Fetching all products - page: {}, size: {}", page, size);
            ProductFilterRequest productFilterRequest = new ProductFilterRequest();
            productFilterRequest.setCategory(category);
            productFilterRequest.setPage(page);
            productFilterRequest.setSize(size);

            List<ProductResponse> products = productService.getAllProducts(productFilterRequest);

            ApiError response = ApiError.builder()
                    .success(true)
                    .message("Products retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .timestamp(LocalDateTime.now())
                    .details(products)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage(), e);
            ApiError response = ApiError.builder()
                    .success(false)
                    .message("Error fetching products: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/get/{productId}", produces = "application/json")
    public ResponseEntity<ApiError> getProductById(
            @Valid @PathVariable("productId")
            @Size(min = 24, max = 24, message = "Product ID must be exactly 24 characters long")
            String productId
    ) {
        try {
            log.info("Fetching product by ID: {}", productId);
            ProductResponse productResponse = productService.getProductById(productId);

            ApiError response = ApiError.builder()
                    .success(true)
                    .message("Product retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .timestamp(LocalDateTime.now())
                    .details(productResponse)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching product by ID {}: {}", productId, e.getMessage(), e);
            ApiError response = ApiError.builder()
                    .success(false)
                    .message("Error fetching product: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
