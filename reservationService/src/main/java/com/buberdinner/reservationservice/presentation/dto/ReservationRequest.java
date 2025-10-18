package com.buberdinner.reservationservice.presentation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull Long dinnerId,
        @NotNull Long guestId,
        @NotNull LocalDateTime reservationDate
) {}

