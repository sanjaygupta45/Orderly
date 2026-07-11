package com.orderflow.auth.controller;

import com.orderflow.auth.dto.LoginRequestDTO;
import com.orderflow.auth.dto.LoginResponseDTO;
import com.orderflow.auth.dto.RegisterRequestDTO;
import com.orderflow.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        LoginResponseDTO response = userService.register(dto);
        response.setSuccess(true);
        response.setMessage("User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = userService.login(dto);
        response.setSuccess(true);
        response.setMessage("Login successful");
        return ResponseEntity.ok(response);
    }
}
