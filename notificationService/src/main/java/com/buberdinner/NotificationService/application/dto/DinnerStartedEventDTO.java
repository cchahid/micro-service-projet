package com.buberdinner.NotificationService.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List; // New import

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DinnerStartedEventDTO {
    private Long dinnerId;
    private Long hostId; // Changed from guestId, assuming it's the dinner's host
    private String restaurantName;
    private LocalDateTime startTime;
    private List<Long> guestIds; // NEW field: list of guests to notify
}