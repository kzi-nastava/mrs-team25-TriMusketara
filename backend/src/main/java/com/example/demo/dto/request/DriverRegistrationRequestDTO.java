package com.example.demo.dto.request;

import com.example.demo.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationRequestDTO {
    private String email;
    private String password;
    private String name;
    private String surname;
    private Gender gender;
    private String address;
    private String phone;

    private VehicleRegistrationRequestDTO vehicle;
}
