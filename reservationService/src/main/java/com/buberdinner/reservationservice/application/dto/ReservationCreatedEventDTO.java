package com.buberdinner.reservationservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event DTO published to Kafka when a reservation is created
 * Sent to 'reservation-created' topic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreatedEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long dinnerId;
    private Long guestId;
    private LocalDateTime reservationTime;
    private String restaurantName;
    private String eventType = "ReservationCreated";
    private LocalDateTime eventTimestamp = LocalDateTime.now();
}

