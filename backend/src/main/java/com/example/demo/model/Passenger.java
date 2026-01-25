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

    @ManyToMany
    @JoinTable(
            name = "passenger_favorite_routes",
            joinColumns = @JoinColumn(name = "passenger_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id")
    )
    // ManyToMany znaci da ce se napraviti posebna tabela sa id-jem putnika i rute koja mu je omiljena
    private List<Route> favoriteRoutes;
}
