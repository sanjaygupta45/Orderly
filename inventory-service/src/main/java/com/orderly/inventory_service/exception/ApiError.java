package com.orderly.inventory_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API error response format
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private Object details;
}

