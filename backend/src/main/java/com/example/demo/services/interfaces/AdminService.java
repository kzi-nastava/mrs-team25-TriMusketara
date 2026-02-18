package com.example.demo.services.interfaces;

import com.example.demo.dto.response.AdminRideHistoryResponseDTO;
import com.example.demo.dto.response.AdminUserResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    List<AdminRideHistoryResponseDTO> getRideHistory(
            Long driverId,
            String userType,
            String sortBy
    );
    List<AdminUserResponseDTO> getAllNonAdminUsers();
}
