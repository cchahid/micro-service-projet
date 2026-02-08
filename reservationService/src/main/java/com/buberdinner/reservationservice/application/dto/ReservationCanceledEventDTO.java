package com.buberdinner.reservationservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Event DTO published to Kafka when a reservation is canceled
 * Sent to 'reservation-canceled' topic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCanceledEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long dinnerId;
    private Long guestId;
    private String eventType = "ReservationCanceled";
}

