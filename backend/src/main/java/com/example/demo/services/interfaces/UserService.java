package com.example.demo.services.interfaces;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.UserRegistrationRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.UserProfileResponseDTO;

public interface UserService {
    LoginResponseDTO login(LoginRequestDTO request);
    UserProfileResponseDTO registerPassenger(UserRegistrationRequestDTO request);
    boolean activatePassenger(String token);
}
