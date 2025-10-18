package com.buberdinner.reservationservice.presentation.mapper;




import com.buberdinner.reservationservice.application.dto.*;
import com.buberdinner.reservationservice.domain.module.Reservation;
import com.buberdinner.reservationservice.domain.valueObject.*;
import com.buberdinner.reservationservice.presentation.dto.ReservationRequest;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponseDto(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId().value(),
                reservation.getDinnerId().value(),
                reservation.getGuestId().value(),
                reservation.getReservationDate()
        );
    }

    public Reservation toDomain(CreateReservationCommand command) {
        return new Reservation(
                ReservationId.generate(),
                new DinnerId(command.dinnerId()),
                new GuestId(command.guestId()),
                command.reservationDate()
        );
    }

    public CreateReservationCommand toCommand(ReservationRequest request) {
        return new CreateReservationCommand(
                request.dinnerId(),
                request.guestId(),
                request.reservationDate()
        );
    }
}
