package com.example.demo.services.interfaces;

import com.example.demo.dto.response.AdminRideHistoryResponseDTO;
import com.example.demo.dto.response.AdminUserResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    List<AdminRideHistoryResponseDTO> getRideHistory(
            Long id,
            String role,
            LocalDateTime from,
            LocalDateTime to,
            String sortBy
    );
    List<AdminUserResponseDTO> getAllNonAdminUsers();
}
