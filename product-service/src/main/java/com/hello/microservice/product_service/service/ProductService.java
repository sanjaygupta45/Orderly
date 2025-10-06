package com.hello.microservice.product_service.service;

import com.hello.microservice.product_service.dto.ProductFilterRequest;
import com.hello.microservice.product_service.dto.ProductRequest;
import com.hello.microservice.product_service.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(String productId);
    List<ProductResponse> getAllProducts(ProductFilterRequest productFilterRequest);
}
