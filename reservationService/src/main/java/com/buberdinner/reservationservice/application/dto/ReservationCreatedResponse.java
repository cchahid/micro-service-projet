package com.buberdinner.reservationservice.application.dto;

import java.util.UUID;

public record ReservationCreatedResponse(
        UUID reservationId,
        String message
) {
    public ReservationCreatedResponse(UUID reservationId) {
        this(reservationId, "Reservation created successfully");
    }

}
