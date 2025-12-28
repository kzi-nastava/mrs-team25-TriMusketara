package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class RideEstimateResponseDTO {
    private List<LocationDTO> route;
    private int estimatedTime;
}
