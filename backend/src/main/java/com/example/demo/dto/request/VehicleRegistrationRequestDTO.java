package com.example.demo.dto.request;

import com.example.demo.model.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRegistrationRequestDTO {
    private String model;
    private VehicleType type;
    private String registration;
    private int seats;
    private boolean isBabyFriendly;
    private boolean isPetFriendly;
}
