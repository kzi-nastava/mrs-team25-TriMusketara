package com.example.demo.dto.request;

import com.example.demo.dto.LocationDTO;
import com.example.demo.model.VehicleType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RideRequestDTO {
    private LocationDTO origin;
    private LocationDTO destination;
    private List<LocationDTO> stops;

    private List<String> passengerEmails;

    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
}
