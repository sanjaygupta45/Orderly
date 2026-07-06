package com.orderflow.auth.exception;

import com.orderflow.auth.dto.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<LoginResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        LoginResponseDTO response = LoginResponseDTO.builder()
                .success(false)
                .message("Validation failed")
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle bad credentials exception
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<LoginResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        LoginResponseDTO response = LoginResponseDTO.builder()
                .success(false)
                .message("Invalid email or password")
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handle username not found exception
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<LoginResponseDTO> handleUsernameNotFound(UsernameNotFoundException ex) {
        LoginResponseDTO response = LoginResponseDTO.builder()
                .success(false)
                .message("User not found")
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<LoginResponseDTO> handleGenericException(Exception ex) {
        LoginResponseDTO response = LoginResponseDTO.builder()
                .success(false)
                .message("An unexpected error occurred")
                .build();
        ex.printStackTrace();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
