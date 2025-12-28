package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RideRequestUnregisteredDTO {
    private LocationDTO origin;
    private LocationDTO destination;
}