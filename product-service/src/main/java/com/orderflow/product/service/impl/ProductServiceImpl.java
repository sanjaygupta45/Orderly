package com.orderflow.product.service.impl;

import com.orderflow.product.dto.ProductRequest;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.models.Product;
import com.orderflow.product.repository.ProductRepository;
import com.orderflow.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        productRepository.findBySkuCode(request.skuCode()).ifPresent(p -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists: " + request.skuCode());
        });
        Product product = Product.builder()
                .skuCode(request.skuCode())
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .price(request.price())
                .build();
        Product saved = productRepository.save(product);
        log.info("Created product {} (sku {})", saved.getId(), saved.getSkuCode());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySku(String skuCode) {
        return productRepository.findBySkuCode(skuCode)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for SKU: " + skuCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll(String category) {
        List<Product> products = StringUtils.hasText(category)
                ? productRepository.findByCategoryIgnoreCase(category)
                : productRepository.findAll();
        return products.stream().map(this::toResponse).toList();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSkuCode(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice());
    }
}
