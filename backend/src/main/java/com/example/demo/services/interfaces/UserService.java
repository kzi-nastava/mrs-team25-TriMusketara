package com.example.demo.services.interfaces;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.UpdateUserProfileRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;

public interface UserService {
    LoginResponseDTO login(LoginRequestDTO request);

    UserProfileResponseDTO getUserProfile(Long id);
    UserProfileResponseDTO changeUserInfo(Long id, UpdateUserProfileRequestDTO request);
    void changePassword(Long id, ChangePasswordRequest request);
}
