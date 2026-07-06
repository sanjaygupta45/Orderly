package com.orderflow.auth.service.Impl;

import com.orderflow.auth.dto.*;
import com.orderflow.auth.entitiy.User;
import com.orderflow.auth.repository.UserRepository;
import com.orderflow.auth.auth.jwt.JwtService;
import com.orderflow.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new BadCredentialsException("User with email " + registerRequestDTO.getEmail() + " already exists");
        }

        User user = new User();
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(registerRequestDTO.getRole());
        user.setActive(true);
        user.setFullName(registerRequestDTO.getFullName());

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole(), savedUser.getId());

        return LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .userId(savedUser.getId())
                .build();
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.getActive()) {
            throw new BadCredentialsException("User account is disabled");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());

        return LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .email(user.getEmail())
                .role(user.getRole())
                .loginAt(new Date().getTime())
                .userId(user.getId())
                .build();
    }

    @Override
    public void logout(Long userId) {
        User user = userRepository.findByUserIdAndActive( userId, true);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

    }


    @Cacheable(value = "users", key = "#userId", unless = "#result == null")
    @Override
    public UserProfileResponseDTO getUserProfile(Long userId) {

        User user = userRepository.findByUserIdAndActive(userId, true);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return mapToProfileDTO(user);
    }

    @CachePut(value = "users", key = "#userId")
    @Override
    public UserProfileResponseDTO updateUserProfile(
            Long userId,
            UpdateUserRequestDTO dto) {

        User user = userRepository.findByUserIdAndActive(userId, true);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new BadCredentialsException("Email " + dto.getEmail() + " is already in use");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        User updated = userRepository.save(user);
        return mapToProfileDTO(updated);
    }


    @CacheEvict(value = "users", key = "#userId")
    @Override
    public void deleteUser(Long UserId) {
        User user = userRepository.findByUserIdAndActive(UserId, true);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        user.setActive(false);
        userRepository.save(user);
    }


    private UserProfileResponseDTO mapToProfileDTO(User user) {
        return UserProfileResponseDTO.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }


}
