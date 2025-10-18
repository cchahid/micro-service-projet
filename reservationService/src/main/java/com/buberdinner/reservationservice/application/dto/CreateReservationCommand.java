package com.buberdinner.reservationservice.application.dto;

import java.time.LocalDateTime;

public record CreateReservationCommand(
        Long dinnerId,
        Long guestId,
        LocalDateTime reservationDate
) {}
