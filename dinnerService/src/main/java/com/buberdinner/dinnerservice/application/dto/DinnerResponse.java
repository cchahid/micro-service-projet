package com.buberdinner.dinnerservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for dinner response in the application layer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DinnerResponse {
    private Long id;
    private Long hostId;
    private Long menuId;
    private String name;
    private String description;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String imageUrl;
    private String address;
    private String cuisineType;
    private int maxGuestCount;
    private String status;
    private long rating;




}
