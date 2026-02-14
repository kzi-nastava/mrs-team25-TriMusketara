package com.example.demo.dto.response;

import com.example.demo.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private String address;
    private String phone;
    private String profileImageUrl;

    private boolean isBlocked;
    private String blockReason;
}
