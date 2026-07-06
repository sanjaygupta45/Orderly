package com.orderflow.product.service;

import com.orderflow.product.dto.ProductRequest;
import com.orderflow.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse getById(Long id);

    ProductResponse getBySku(String skuCode);

    // category is optional; null/blank returns everything
    List<ProductResponse> getAll(String category);
}
