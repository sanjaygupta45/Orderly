package com.orderly.auth_service.service;

import com.orderly.auth_service.dto.LoginRequestDTO;
import com.orderly.auth_service.dto.LoginResponseDTO;
import com.orderly.auth_service.dto.RegisterRequestDTO;

public interface UserService {
    LoginResponseDTO register(RegisterRequestDTO registerRequestDTO);
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    void logout(String email);
}
