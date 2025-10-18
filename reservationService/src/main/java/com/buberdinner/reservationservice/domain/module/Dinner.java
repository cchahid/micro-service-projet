package com.buberdinner.reservationservice.domain.module;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Getter
@Setter
public class Dinner {
    private Long id;
    private Long hostId;
    private Long menuId;
    private String name;
    private String description;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String address;
    private String cuisineType;
    private int maxGuestCount;
    private String status;
    private long rating;

    public Dinner(){

    }

    public Dinner(Long id, Long hostId, Long menuId, String name, String description, double price, LocalDateTime startTime, LocalDateTime endTime, String address, String cuisineType, int maxGuestCount, String status, long rating) {
        this.id = id;
        this.hostId = hostId;
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
        this.cuisineType = cuisineType;
        this.maxGuestCount = maxGuestCount;
        this.status = status;
        this.rating = rating;
    }
}
