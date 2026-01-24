package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    @NotNull(message = "Longitude cannot be null")
    private double longitude;

    @NotNull(message = "Latitude cannot be null")
    private double latitude;

    @NotBlank(message = "Address cannot be blank")
    private String address;
}
