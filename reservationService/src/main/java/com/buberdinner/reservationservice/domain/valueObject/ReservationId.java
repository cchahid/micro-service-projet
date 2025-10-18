package com.buberdinner.reservationservice.domain.valueObject;

import java.util.Objects;
import java.util.UUID;

public record ReservationId(UUID value) {
    public ReservationId {
        Objects.requireNonNull(value);
    }

    public static ReservationId generate() {
        return new ReservationId(UUID.randomUUID());
    }

    @Override
    public UUID value() {
        return value;
    }
}
