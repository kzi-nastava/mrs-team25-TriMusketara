package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("PASSENGER")
public class Passenger extends User {

    @ElementCollection
    @CollectionTable(
            name = "passenger_linked_emails",
            joinColumns = @JoinColumn(name = "passenger_id")
    )
    @Column(name = "email")
    private List<String> linkedEmails;

    private List<Route> favoriteRoutes;
}
