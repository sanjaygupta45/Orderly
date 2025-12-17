package com.orderly.order_service.service;

import com.orderly.order_service.client.InventoryClient;
import com.orderly.order_service.dto.ApiResponse;
import com.orderly.order_service.dto.OrderRequest;
import com.orderly.order_service.model.Order;
import com.orderly.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public ApiResponse placeOrder(OrderRequest orderRequest) {
        try {
            log.info("Placing order for SKU: {}, Quantity: {}", orderRequest.skuCode(), orderRequest.quantity());

            // Check inventory synchronously
            boolean inStock = inventoryClient.checkStock(orderRequest.skuCode(), orderRequest.quantity())
                    .toCompletableFuture()
                    .join();

            if (!inStock) {
                log.warn("Product out of stock - SKU: {}, Quantity: {}", orderRequest.skuCode(),
                        orderRequest.quantity());
                return ApiResponse.builder()
                        .success(false)
                        .message("Product is out of stock")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // Save order if inventory check passes
            var order = mapToOrder(orderRequest);
            orderRepository.save(order);
            log.info("Order placed successfully - Order Number: {}", order.getOrderNumber());

            return ApiResponse.builder()
                    .success(true)
                    .message("Order placed successfully")
                    .status(HttpStatus.CREATED.value())
                    .timestamp(LocalDateTime.now())
                    .details(order) // Optionally return the order details
                    .build();

        } catch (Exception e) {
            log.error("Error placing order for SKU: {}", orderRequest.skuCode(), e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Failed to place order: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
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
