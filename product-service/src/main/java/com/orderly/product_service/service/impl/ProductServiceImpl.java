package com.orderly.product_service.service.impl;

import com.orderly.product_service.service.ProductService;
import com.orderly.product_service.dto.ProductFilterRequest;
import com.orderly.product_service.dto.ProductRequest;
import com.orderly.product_service.dto.ProductResponse;
import com.orderly.product_service.models.Product;
import com.orderly.product_service.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Product Service Implementation
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating product: {}", productRequest.name());
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice()
        );
    }

    @Override
    public ProductResponse getProductById(String productId) {
        log.info("Fetching product by ID: {}", productId);

        if (!StringUtils.hasLength(productId)) {
            throw new IllegalArgumentException("Product ID must not be null or empty");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new IllegalArgumentException("Product with ID " + productId + " not found");
                });

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }

    @Override
    public List<ProductResponse> getAllProducts(ProductFilterRequest productFilterRequest) {
        log.info("Fetching all products - page: {}, size: {}",
                productFilterRequest.getPage(), productFilterRequest.getSize());

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()
                ))
                .toList();
    }
}
