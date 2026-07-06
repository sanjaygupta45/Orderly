package com.orderflow.shared.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

// Uniform envelope for error responses. Includes the correlation id so a user
// can quote it and we can find the exact request in the logs.
@Getter
@Builder
@AllArgsConstructor
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;

    @Builder.Default
    private Instant timestamp = Instant.now();

    private String correlationId;
}
