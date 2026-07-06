package com.orderflow.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// userId is taken from the request for now; from Phase 3 it comes from the JWT.
public record CreateOrderRequest(
        @NotNull Long userId,
        @NotEmpty List<@Valid OrderItemRequest> items) {
}
