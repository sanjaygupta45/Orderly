package com.orderflow.product.service.impl;

import com.orderflow.product.service.ProductService;
import com.orderflow.product.dto.ProductRequest;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.exception.ApiError;
import com.orderflow.product.models.Product;
import com.orderflow.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

        private final ProductRepository productRepository;

        @Override
        public ApiError createProduct(ProductRequest productRequest, String requestPath) {
                log.info("Creating product: {}", productRequest.name());

                Product product = Product.builder()
                                .name(productRequest.name())
                                .description(productRequest.description())
                                .price(productRequest.price())
                                .build();

                Product savedProduct = productRepository.save(product);
                log.info("Product created successfully with ID: {}", savedProduct.getId());

                ProductResponse productResponse = mapToProductResponse(savedProduct);

                return ApiError.builder()
                                .success(true)
                                .message("Product created successfully")
                                .status(HttpStatus.CREATED.value())
                                .timestamp(LocalDateTime.now())
                                .path(requestPath)
                                .details(productResponse)
                                .build();
        }

        @Override
        public ApiError getProductById(String productId, String requestPath) {
                log.info("Fetching product by ID: {}", productId);

                if (!StringUtils.hasLength(productId)) {
                        throw new IllegalArgumentException("Product ID must not be null or empty");
                }

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> {
                                        log.warn("Product not found with ID: {}", productId);
                                        return new IllegalArgumentException(
                                                        "Product with ID " + productId + " not found");
                                });

                ProductResponse productResponse = mapToProductResponse(product);

                return ApiError.builder()
                                .success(true)
                                .message("Product retrieved successfully")
                                .status(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .path(requestPath)
                                .details(productResponse)
                                .build();
        }

        @Override
        public ApiError getAllProducts(String category, int page, int size, String requestPath) {
                log.info("Fetching all products - page: {}, size: {}", page, size);

                List<Product> products = productRepository.findAll();

                List<ProductResponse> productResponses = products.stream()
                                .map(this::mapToProductResponse)
                                .toList();

                return ApiError.builder()
                                .success(true)
                                .message("Products retrieved successfully")
                                .status(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .path(requestPath)
                                .details(productResponses)
                                .build();
        }

        private ProductResponse mapToProductResponse(Product product) {
                return new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getPrice());
        }
}
