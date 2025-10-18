package com.buberdinner.reservationservice.infrastructure.entity;


import com.buberdinner.reservationservice.domain.module.Reservation;
import com.buberdinner.reservationservice.domain.valueObject.DinnerId;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Data
public class ReservationEntity {
    @Id
    private UUID id;

    @Column(name = "dinner_id", nullable = false)
    private Long dinnerId;

    @Column(name = "guest_id", nullable = false)
    private Long guestId;

    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;

    public ReservationEntity(UUID id, Long dinnerId, Long guestId, LocalDateTime reservationDate) {
        this.id = id;
        this.dinnerId = dinnerId;
        this.guestId = guestId;
        this.reservationDate = reservationDate;
    }

    public ReservationEntity() {

    }

    public static ReservationEntity fromDomain(Reservation reservation) {
        return new ReservationEntity(
                reservation.getId().value(),
                reservation.getDinnerId().value(),
                reservation.getGuestId().value(),
                reservation.getReservationDate()
        );
    }

    public Reservation toDomain() {
        return new Reservation(
                new ReservationId(id),
                new DinnerId(dinnerId),
                new GuestId(guestId),
                reservationDate
        );
    }
}
