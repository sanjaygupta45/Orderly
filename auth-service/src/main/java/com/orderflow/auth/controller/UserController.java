package com.orderflow.auth.controller;

import com.orderflow.auth.dto.*;
import com.orderflow.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO dto) {

        LoginResponseDTO response = userService.register(dto);
        response.setSuccess(true);
        response.setMessage("User registered successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        LoginResponseDTO response = userService.login(dto);
        response.setSuccess(true);
        response.setMessage("Login successful");

        return ResponseEntity.ok(response);
    }

    // ================= LOGOUT =================

    @PostMapping("/logout/{userId}")
    public ResponseEntity<LoginResponseDTO> logout(
            @PathVariable Long userId) {

        userService.logout(userId);

        LoginResponseDTO response = LoginResponseDTO.builder()
                .success(true)
                .message("Logout successful")
                .build();

        return ResponseEntity.ok(response);
    }

    // ================= GET PROFILE =================

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getProfile(
            @PathVariable Long userId) {

        UserProfileResponseDTO profile =
                userService.getUserProfile(userId);

        return ResponseEntity.ok(profile);
    }

    // ================= UPDATE PROFILE =================

    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequestDTO dto) {

        UserProfileResponseDTO updated =
                userService.updateUserProfile(userId, dto);

        return ResponseEntity.ok(updated);
    }

    // ================= DELETE USER =================

    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.ok("User deleted successfully");
    }
}