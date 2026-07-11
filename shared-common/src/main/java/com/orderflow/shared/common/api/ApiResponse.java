package com.orderflow.shared.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

// Uniform envelope for successful REST responses across all services.
// (Errors use ApiError, built by the shared exception handler.)
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Instant timestamp;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data, Instant.now());
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }
}
