package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("DRIVER")
public class Driver extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;
}
