package com.orderly.order_service.controller;


import com.orderly.order_service.dto.OrderRequest;
import com.orderly.order_service.exception.ApiError;
import com.orderly.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Order Controller - Handles order placement requests
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    /**
     * Place a new order.
     * Validates inventory before accepting the order.
     *
     * @param orderRequest Order details (skuCode, quantity, price)
     * @return Success/failure response
     */
    @PostMapping
    public ResponseEntity<ApiError> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            log.info("Received order request for SKU: {}", orderRequest.skuCode());
            orderService.placeOrder(orderRequest);

            ApiError response = ApiError.builder()
                    .success(true)
                    .message("Order placed successfully")
                    .status(HttpStatus.CREATED.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order request: {}", e.getMessage());
            ApiError response = ApiError.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
