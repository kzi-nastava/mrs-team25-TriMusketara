package com.example.demo.dto.response;

import com.example.demo.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class LoginResponseDTO {
    private Long userId;
    private String role;
    private boolean active;
}
