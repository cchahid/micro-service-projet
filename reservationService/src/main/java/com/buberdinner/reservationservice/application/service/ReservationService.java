package com.buberdinner.reservationservice.application.service;

import com.buberdinner.reservationservice.application.dto.CreateReservationCommand;
import com.buberdinner.reservationservice.application.dto.ReservationCreatedResponse;
import com.buberdinner.reservationservice.application.dto.ReservationResponse;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;

import java.util.Optional;
import java.util.UUID;

public interface ReservationService {
    ReservationCreatedResponse createReservation(CreateReservationCommand command);
    public ReservationResponse getReservation(ReservationId reservationId);
    void cancelReservation(ReservationId reservationId);
    Optional<ReservationResponse> getReservationsByGuestId(GuestId id);
    Optional<ReservationResponse> getReservationsByDinnerId(Long id);
}
