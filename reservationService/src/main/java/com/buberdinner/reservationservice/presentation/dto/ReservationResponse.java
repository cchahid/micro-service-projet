package com.buberdinner.reservationservice.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        Long dinnerId,
        Long guestId,
        LocalDateTime reservationDate
) {}
