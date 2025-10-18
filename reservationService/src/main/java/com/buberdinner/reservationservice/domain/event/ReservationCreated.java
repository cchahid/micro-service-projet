package com.buberdinner.reservationservice.domain.event;

import java.io.Serializable;


public record ReservationCreated(
        long dinnerId,
        long guestId
) implements Serializable {
}
