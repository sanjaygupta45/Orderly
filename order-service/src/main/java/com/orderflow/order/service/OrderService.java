package com.orderflow.order.service;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getByOrderId(String orderId);

    List<OrderResponse> getByUserId(Long userId);
}
