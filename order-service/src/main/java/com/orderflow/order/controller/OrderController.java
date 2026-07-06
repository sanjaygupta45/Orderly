package com.orderflow.order.controller;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.order.service.OrderService;
import com.orderflow.shared.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // Create an order. Returns immediately with PENDING_PAYMENT; the saga finishes async.
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Order created", response));
    }

    // Poll this to track the order status as the saga progresses.
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> get(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getByOrderId(orderId)));
    }

    // Order history for a user.
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> byUser(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getByUserId(userId)));
    }
}
