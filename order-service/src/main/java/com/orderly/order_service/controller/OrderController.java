package com.orderly.order_service.controller;

import com.orderly.order_service.dto.OrderRequest;
import com.orderly.order_service.dto.OrderResponse;
import com.orderly.order_service.exception.ApiError;
import com.orderly.order_service.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> placeOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {
        try {
            log.info("Received order request for SKU: {}", orderRequest.skuCode());
            OrderResponse response = orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            log.error("Unexpected error during order placement", ex);
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An unexpected error occurred",
                    request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
