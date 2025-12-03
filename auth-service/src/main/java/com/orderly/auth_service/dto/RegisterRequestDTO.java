package com.orderly.auth_service.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RegisterRequestDTO {
    private String email;
    private String password;
    private Set<String> roles;
}
