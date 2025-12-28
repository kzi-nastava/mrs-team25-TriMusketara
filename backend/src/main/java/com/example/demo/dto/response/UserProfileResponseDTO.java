package com.example.demo.dto.response;

import com.example.demo.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private Gender gender;
    private String address;
    private String phone;
}
