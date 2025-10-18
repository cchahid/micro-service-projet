package com.buberdinner.reservationservice.domain.event;

import java.io.Serializable;

public record ReservationCanceled(
        long dinnerId,
        long guestId
) implements Serializable {

}
