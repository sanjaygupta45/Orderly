package com.orderflow.auth.service;

import com.orderflow.auth.dto.*;

public interface UserService {

    LoginResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    void logout(Long userId);

    UserProfileResponseDTO getUserProfile(Long userId);

    UserProfileResponseDTO updateUserProfile(Long userId, UpdateUserRequestDTO dto);

    void deleteUser(Long userId);
}
