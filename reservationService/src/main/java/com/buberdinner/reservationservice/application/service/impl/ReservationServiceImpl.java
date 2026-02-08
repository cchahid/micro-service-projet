package com.buberdinner.reservationservice.application.service.impl;

import com.buberdinner.reservationservice.application.dto.CreateReservationCommand;
import com.buberdinner.reservationservice.application.dto.ReservationCreatedResponse;
import com.buberdinner.reservationservice.application.dto.ReservationResponse;
import com.buberdinner.reservationservice.application.service.ReservationService;
import com.buberdinner.reservationservice.domain.event.ReservationCanceled;
import com.buberdinner.reservationservice.domain.event.ReservationCreated;
import com.buberdinner.reservationservice.domain.module.Reservation;
import com.buberdinner.reservationservice.domain.repository.ReservationRepository;
import com.buberdinner.reservationservice.domain.valueObject.DinnerId;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReservationServiceImpl implements ReservationService {



    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final com.buberdinner.reservationservice.presentation.mapper.ReservationMapper reservationMapper = new com.buberdinner.reservationservice.presentation.mapper.ReservationMapper();

    public ReservationServiceImpl(ReservationRepository reservationRepository, ApplicationEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ReservationCreatedResponse createReservation(CreateReservationCommand command) {
         Reservation reservation = reservationRepository.createReservation(new Reservation(ReservationId.generate(),
                 DinnerId.generate(command.dinnerId()),
                 GuestId.generate(command.guestId())));
         eventPublisher.publishEvent(new ReservationCreated(
                 reservation.getId().value(),
                 reservation.getDinnerId().value(),
                 reservation.getGuestId().value(),
                 reservation.getReservationDate(),
                 command.restaurantName() != null ? command.restaurantName() : "Unknown Restaurant"
                 )
         );
         return  new ReservationCreatedResponse(reservation.getId().value());
    }

    @Override
    public ReservationResponse getReservation(ReservationId reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservationMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found for id: " + reservationId));
    }

    @Override
    public void cancelReservation(ReservationId reservationId) {
        Optional<Reservation> reservation= reservationRepository.findById(reservationId);
        if(reservation.isEmpty()){
            throw new IllegalArgumentException("Reservation not found for id: " + reservationId);
        }
        eventPublisher.publishEvent(new ReservationCanceled(
                reservation.get().getId().value(),
                reservation.get().getDinnerId().value(),
                reservation.get().getGuestId().value()
        ));
        reservationRepository.deleteReservation(reservationId);
    }

    @Override
    public Optional<ReservationResponse> getReservationsByGuestId(GuestId id) {
        Optional<List<Reservation>> responses = reservationRepository.findByGuestId(id);
        return responses.stream().map(a -> reservationMapper.toResponseDto(a.get(0)) ).findFirst();
    }

    @Override
    public Optional<ReservationResponse> getReservationsByDinnerId(Long id) {
        Optional<List<Reservation>> responses = reservationRepository.findAllByDinnerId(DinnerId.generate(id));
        return responses.stream().map(a -> reservationMapper.toResponseDto(a.get(0)) ).findFirst();
    }
}
