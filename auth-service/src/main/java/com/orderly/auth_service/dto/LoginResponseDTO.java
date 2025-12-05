package com.orderly.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String email;
    private String role;
    private Object data;
    private Long timestamp;
}
