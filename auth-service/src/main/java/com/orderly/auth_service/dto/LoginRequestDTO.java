package com.orderly.auth_service.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String accessToken;
    private String tokenType = "Bearer";
}
