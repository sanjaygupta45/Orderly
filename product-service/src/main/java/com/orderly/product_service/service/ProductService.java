package com.orderly.product_service.service;

import com.orderly.product_service.dto.ProductRequest;
import com.orderly.product_service.exception.ApiError;

public interface ProductService {
    ApiError createProduct(ProductRequest productRequest, String requestPath);

    ApiError getProductById(String productId, String requestPath);

    ApiError getAllProducts(String category, int page, int size, String requestPath);
}
