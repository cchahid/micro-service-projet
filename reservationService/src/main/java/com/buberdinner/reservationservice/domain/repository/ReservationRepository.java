package com.buberdinner.reservationservice.domain.repository;


import com.buberdinner.reservationservice.domain.module.Reservation;
import com.buberdinner.reservationservice.domain.valueObject.DinnerId;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation createReservation(Reservation reservation);
    Optional<Reservation> findById(ReservationId id);
    void deleteReservation(ReservationId id);
    Optional<List<Reservation>> findByGuestId(GuestId guestId);
    Optional<List<Reservation>> findAllByDinnerId(DinnerId dinnerId);
}
