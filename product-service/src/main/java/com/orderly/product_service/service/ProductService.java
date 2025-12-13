package com.orderly.product_service.service;

import com.orderly.product_service.dto.ProductFilterRequest;
import com.orderly.product_service.dto.ProductRequest;
import com.orderly.product_service.dto.ProductResponse;

import java.util.List;

/**
 * Product Service Interface - Defines product catalog operations
 */
public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(String productId);
    List<ProductResponse> getAllProducts(ProductFilterRequest productFilterRequest);
}
