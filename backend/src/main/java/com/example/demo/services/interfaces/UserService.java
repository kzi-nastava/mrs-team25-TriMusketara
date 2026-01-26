package com.example.demo.services.interfaces;

import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;

public interface UserService {
    LoginResponseDTO login(LoginRequestDTO request);
}
