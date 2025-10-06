package com.hello.microservice.product_service.service.impl;

import com.hello.microservice.product_service.dto.ProductFilterRequest;
import com.hello.microservice.product_service.dto.ProductRequest;
import com.hello.microservice.product_service.dto.ProductResponse;
import com.hello.microservice.product_service.models.Product;
import com.hello.microservice.product_service.repository.ProductRepository;
import com.hello.microservice.product_service.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder ()
                .name (productRequest.name ())
                .description (productRequest.description ())
                .price (productRequest.price ())
                .build ();

        Product savedProduct = productRepository.save(product);
        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice()
        );
    }

    @Override
    public ProductResponse getProductById(String productId) {
        if (!StringUtils.hasLength (productId)) {
            throw new IllegalArgumentException ("Product ID must not be null or empty");
        }
        Product product = productRepository.findById (productId)
                .orElseThrow (() -> new IllegalArgumentException ("Product with ID " + productId + " not found"));
        return new ProductResponse (product.getId (), product.getName (), product.getDescription (), product.getPrice ());
    }

    @Override
    public List<ProductResponse> getAllProducts(ProductFilterRequest productFilterRequest) {
        List<Product> products = productRepository.findAll ();
        return products.stream ()
                .map (product -> new ProductResponse (product.getId (), product.getName (), product.getDescription (), product.getPrice ()))
                .toList ();
    }
}
