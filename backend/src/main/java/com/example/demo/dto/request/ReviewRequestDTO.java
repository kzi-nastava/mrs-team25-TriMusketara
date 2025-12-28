package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReviewRequestDTO {
    private int driverRating;  // 1-5
    private int vehicleRating; // 1-5
    private String comment;
}