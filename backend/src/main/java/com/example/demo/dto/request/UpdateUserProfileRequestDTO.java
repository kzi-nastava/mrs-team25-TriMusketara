package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequestDTO {
    private String name;
    private String surname;
    private String address;
    private String phone;

    // private String email;
    // private String password;
}
