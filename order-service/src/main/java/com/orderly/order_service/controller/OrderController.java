package com.orderly.order_service.controller;

import com.orderly.order_service.dto.ApiResponse;
import com.orderly.order_service.dto.OrderRequest;
import com.orderly.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Received order request for SKU: {}", orderRequest.skuCode());
        ApiResponse response = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}
