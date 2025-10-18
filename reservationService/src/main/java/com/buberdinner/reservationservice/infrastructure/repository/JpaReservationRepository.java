package com.buberdinner.reservationservice.infrastructure.repository;

import com.buberdinner.reservationservice.domain.module.Reservation;
import com.buberdinner.reservationservice.domain.repository.ReservationRepository;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;
import com.buberdinner.reservationservice.infrastructure.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaReservationRepository extends JpaRepository<ReservationEntity, UUID>{
    List<ReservationEntity> findAllByDinnerId(long dinnerId);
    List<ReservationEntity> findAllByGuestId(long guestId);
}
