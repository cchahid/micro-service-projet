package com.buberdinner.reservationservice.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


public record ReservationCreated(
        UUID reservationId,
        long dinnerId,
        long guestId,
        LocalDateTime reservationTime,
        String restaurantName
) implements Serializable {
}
