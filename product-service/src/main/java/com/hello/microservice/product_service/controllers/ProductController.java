package com.hello.microservice.product_service.controllers;

import com.hello.microservice.product_service.dto.ProductFilterRequest;
import com.hello.microservice.product_service.dto.ProductRequest;
import com.hello.microservice.product_service.dto.ProductResponse;
import com.hello.microservice.product_service.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        try {
            ProductResponse productResponse = productService.createProduct (productRequest);
            return ResponseEntity.ok (productResponse);
        } catch (Exception e) {
            log.error ("Error creating product: {}", e.getMessage ());
            throw e;
        }
    }

    @GetMapping(path = "/get/all", produces = "application/json")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(value = "category", required = false) String category, // default request param required is true
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            ProductFilterRequest productFilterRequest = new ProductFilterRequest ();

            // Set filter parameters if provided
            productFilterRequest.setCategory (category);
            productFilterRequest.setPage (page);
            productFilterRequest.setSize (size);
            List<ProductResponse> products = productService.getAllProducts (productFilterRequest);
            return ResponseEntity.ok (products);
        } catch (Exception e) {
            log.error ("Error fetching products: {}", e.getMessage ());
            throw e;
        }
    }

    @GetMapping(path = "/get/{productId}", produces = "application/json")
    public ResponseEntity<ProductResponse> getProductById(
            @Valid @PathVariable("productId")
            @Size(min = 24, max = 24, message = "Product ID must be exactly 24 characters long")
            String productId
    ) {
        try {
            ProductResponse productResponse = productService.getProductById (productId);
            return ResponseEntity.ok (productResponse);
        } catch (Exception e) {
            log.error ("Error fetching product by ID {}: {}", productId, e.getMessage ());
            throw e;
        }
    }

}
