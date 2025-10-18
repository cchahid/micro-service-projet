package com.buberdinner.NotificationService.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID; // New import for UUID

// DTO for the ReservationCreated event
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreatedEventDTO {
    private UUID reservationId; // Changed from String to UUID
    private Long guestId;
    private Long dinnerId;
    private LocalDateTime reservationTime;
    private String restaurantName;
    // Add other relevant fields from the reservation service
}