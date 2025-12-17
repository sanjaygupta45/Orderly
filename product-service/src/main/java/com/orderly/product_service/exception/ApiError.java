package com.orderly.product_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
