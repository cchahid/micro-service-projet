package com.buberdinner.reservationservice.infrastructure.repository.impl;

import com.buberdinner.reservationservice.domain.module.Reservation;
import com.buberdinner.reservationservice.domain.repository.ReservationRepository;
import com.buberdinner.reservationservice.domain.valueObject.DinnerId;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;
import com.buberdinner.reservationservice.infrastructure.entity.ReservationEntity;
import com.buberdinner.reservationservice.infrastructure.repository.JpaReservationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryimpl implements ReservationRepository {

    private JpaReservationRepository jpaReservationRepository;
    public ReservationRepositoryimpl(JpaReservationRepository jpaReservationRepository) {
        this.jpaReservationRepository = jpaReservationRepository;
    }

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        ReservationEntity reservationEntity = ReservationEntity.fromDomain(reservation);
        ReservationEntity saved = jpaReservationRepository.save(reservationEntity);
        return saved.toDomain();
    }

    @Override
    public Optional<Reservation> findById(ReservationId id) {
        return jpaReservationRepository.findById(id.value()).map(ReservationEntity::toDomain);
    }

    @Override
    public void deleteReservation(ReservationId id) {
         jpaReservationRepository.deleteById(id.value());
    }

    @Override
    public Optional<List<Reservation>> findByGuestId(GuestId guestId) {
        return Optional.of(jpaReservationRepository.findAllByGuestId(guestId.value()).stream()
                .map(ReservationEntity::toDomain).toList());
    }

    @Override
    public Optional<List<Reservation>> findAllByDinnerId(DinnerId dinnerId) {
        return Optional.of(jpaReservationRepository.findAllByDinnerId(dinnerId.value()).stream()
                .map(ReservationEntity::toDomain).toList());
    }
}
