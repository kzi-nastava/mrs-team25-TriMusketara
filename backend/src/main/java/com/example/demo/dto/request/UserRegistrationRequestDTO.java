package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequestDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private String address;
    private String phoneNumber;
}
