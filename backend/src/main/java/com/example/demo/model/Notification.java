package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;  // who the notifications belong to

    @Column(nullable = false)
    private String content;

    private Long rideId;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification(Long userId, String content, Long rideId) {
        this.userId = userId;
        this.content = content;
        this.rideId = rideId;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
}
