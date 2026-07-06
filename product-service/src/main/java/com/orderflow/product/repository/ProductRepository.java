package com.orderflow.product.repository;

import com.orderflow.product.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuCode(String skuCode);

    List<Product> findByCategoryIgnoreCase(String category);
}
