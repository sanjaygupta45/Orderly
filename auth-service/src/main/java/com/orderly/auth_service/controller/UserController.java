package com.orderly.auth_service.controller;

import com.orderly.auth_service.dto.LoginRequestDTO;
import com.orderly.auth_service.dto.LoginResponseDTO;
import com.orderly.auth_service.dto.RegisterRequestDTO;
import com.orderly.auth_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        LoginResponseDTO response = userService.register(registerRequestDTO);
        response.setSuccess(true);
        response.setMessage("User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = userService.login(loginRequestDTO);
        response.setSuccess(true);
        response.setMessage("Login successful");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDTO> logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            userService.logout(email);
            LoginResponseDTO response = LoginResponseDTO.builder()
                    .success(true)
                    .message("Logout successful")
                    .timestamp(System.currentTimeMillis())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        LoginResponseDTO response = LoginResponseDTO.builder()
                .success(false)
                .message("User not authenticated")
                .timestamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
