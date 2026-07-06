package com.orderflow.product.service;

import com.orderflow.product.dto.ProductRequest;
import com.orderflow.product.exception.ApiError;

public interface ProductService {
    ApiError createProduct(ProductRequest productRequest, String requestPath);

    ApiError getProductById(String productId, String requestPath);

    ApiError getAllProducts(String category, int page, int size, String requestPath);
}
