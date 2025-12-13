package com.orderly.order_service.service;


import com.orderly.order_service.client.InventoryClient;
import com.orderly.order_service.dto.OrderRequest;
import com.orderly.order_service.model.Order;
import com.orderly.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    /**
     * Place an order with inventory validation.
     * Checks stock before saving the order.
     */
    public void placeOrder(OrderRequest orderRequest) {
        try {
            log.info("Placing order for SKU: {}, Quantity: {}", orderRequest.skuCode(), orderRequest.quantity());

            // Check inventory synchronously
            boolean inStock = inventoryClient.checkStock(orderRequest.skuCode(), orderRequest.quantity())
                    .toCompletableFuture()
                    .join();

            if (!inStock) {
                log.warn("Product out of stock - SKU: {}, Quantity: {}", orderRequest.skuCode(), orderRequest.quantity());
                throw new IllegalArgumentException("Product is out of stock");
            }

            // Save order if inventory check passes
            var order = mapToOrder(orderRequest);
            orderRepository.save(order);
            log.info("Order placed successfully - Order Number: {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Error placing order for SKU: {}", orderRequest.skuCode(), e);
            throw new RuntimeException("Failed to place order: " + e.getMessage(), e);
        }
    }

    private static Order mapToOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        order.setSkuCode(orderRequest.skuCode());
        return order;
    }
}
