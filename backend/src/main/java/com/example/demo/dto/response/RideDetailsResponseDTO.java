package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideDetailsResponseDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean canceled;
    private String canceledBy;
    private double price;
    private boolean panicTriggered;

    // Driver - basic info
    private Long driverId;
    private String driverName;

    // Passengers
    private List<UserProfileResponseDTO> passengers;

    // Review info
    private Integer rating;
    private String comment;
}
