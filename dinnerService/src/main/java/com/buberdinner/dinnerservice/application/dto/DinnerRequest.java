package com.buberdinner.dinnerservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DinnerRequest {
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
}
