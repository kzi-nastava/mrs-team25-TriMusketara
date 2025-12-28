package com.example.demo.dto.request;

import com.example.demo.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideRequestUnregisteredDTO {
    private LocationDTO origin;
    private LocationDTO destination;
}