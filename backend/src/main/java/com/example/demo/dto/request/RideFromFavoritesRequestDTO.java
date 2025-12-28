package com.example.demo.dto.request;

import com.example.demo.model.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideFromFavoritesRequestDTO {
    private Long favoriteRideId;

    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;

}
