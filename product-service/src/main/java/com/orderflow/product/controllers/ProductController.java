package com.orderflow.product.controllers;

import com.orderflow.product.service.ProductService;
import com.orderflow.product.dto.ProductRequest;
import com.orderflow.product.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ApiError> createProduct(
            @Valid @RequestBody ProductRequest productRequest,
            HttpServletRequest request) {
        try {
            log.info("Creating product: {}", productRequest.name());
            ApiError response = productService.createProduct(productRequest, request.getRequestURI());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            ApiError response = ApiError.builder()
                    .success(false)
                    .message("Error creating product: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/get/all", produces = "application/json")
    public ResponseEntity<ApiError> getAllProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            log.info("Fetching all products - page: {}, size: {}", page, size);
            ApiError response = productService.getAllProducts(category, page, size, request.getRequestURI());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage(), e);
            ApiError response = ApiError.builder()
                    .success(false)
                    .message("Error fetching products: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/get/{productId}", produces = "application/json")
    public ResponseEntity<ApiError> getProductById(
            @Valid @PathVariable("productId") @Size(min = 24, max = 24, message = "Product ID must be exactly 24 characters long") String productId,
            HttpServletRequest request) {
        try {
            log.info("Fetching product by ID: {}", productId);
            ApiError response = productService.getProductById(productId, request.getRequestURI());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching product by ID {}: {}", productId, e.getMessage(), e);
            ApiError response = ApiError.builder()
                    .success(false)
                    .message("Error fetching product: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
