package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private List<Ride> rides;
}
