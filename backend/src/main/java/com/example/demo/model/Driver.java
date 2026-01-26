package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.core.annotation.Order;

import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("DRIVER")
public class Driver extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToMany(mappedBy = "driver")
    @Where(clause = "status = 'SCHEDULED'")
    @OrderBy("scheduledTime ASC")
    private List<Ride> scheduledRides;

    @OneToOne
    private Ride activeRide;

    private int workMinutes;

    // For now, only to see if driver registration works
    // Will be changed when others finish authorization
    @Column(name = "registration_token")
    private String registrationToken;
}
