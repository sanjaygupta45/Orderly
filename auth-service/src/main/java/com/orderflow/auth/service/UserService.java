package com.orderflow.auth.service;

import com.orderflow.auth.dto.LoginRequestDTO;
import com.orderflow.auth.dto.LoginResponseDTO;
import com.orderflow.auth.dto.RegisterRequestDTO;

// auth-service does two things: create accounts and issue JWTs.
public interface UserService {

    LoginResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
