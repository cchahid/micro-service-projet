package com.buberdinner.dinnerservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class DinnerUpdateRequest {
    private Long Dinnerid;
    private String name;
    private String description;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String cuisineType;
    private int maxGuestCount;
}
