package com.buberdinner.NotificationService.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCanceledEventDTO {
    private UUID reservationId;
    private Long guestId;
}