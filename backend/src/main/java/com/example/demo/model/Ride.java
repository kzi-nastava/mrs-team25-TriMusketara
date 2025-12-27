package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Location currentLocation;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime scheduledTime;

    @ManyToOne
    @JoinColumn(name = "cancelled_by_id")
    private User cancelledBy;

    private String cancellationReason;

    @ManyToMany
    private List<Passenger> passengers;
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private double price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_id")
    private Review review;

}
