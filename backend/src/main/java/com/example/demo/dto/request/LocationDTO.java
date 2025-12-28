package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private double xCoord;
    private double yCoord;
    private String address;
}
