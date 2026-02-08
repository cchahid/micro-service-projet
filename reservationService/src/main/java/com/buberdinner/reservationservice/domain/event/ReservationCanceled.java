package com.buberdinner.reservationservice.domain.event;

import java.io.Serializable;
import java.util.UUID;

public record ReservationCanceled(
        UUID reservationId,
        long dinnerId,
        long guestId
) implements Serializable {

}
