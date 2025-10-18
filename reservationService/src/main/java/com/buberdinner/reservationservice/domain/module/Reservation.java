package com.buberdinner.reservationservice.domain.module;

import com.buberdinner.reservationservice.domain.valueObject.DinnerId;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;


public class Reservation {
    private final ReservationId id;
    private final DinnerId dinnerId;
    private final GuestId guestId;
    private final LocalDateTime reservationDate;

    //private final List<Object> domainEventsList = new ArrayList<>();

//    private static final Logger log = LoggerFactory.getLogger(ReservationEventsListener.class);


//    @DomainEvents
//    public List<Object> domainEvents() {
//        log.info("Publication de {} événement(s)", domainEventsList.size()); // <-- Nouveau log
//        return List.copyOf(domainEventsList);
//    }
//
//    @AfterDomainEventPublication
//    public void clearDomainEvents() {
//        log.info("Nettoyage des événements"); // <-- Nouveau log
//        domainEventsList.clear();
//    }


    public Reservation(ReservationId id, DinnerId dinnerId, GuestId guestId) {
        this.id = Objects.requireNonNull(id);
        this.dinnerId = Objects.requireNonNull(dinnerId);
        this.guestId = Objects.requireNonNull(guestId);
        this.reservationDate = LocalDateTime.now();

    }

    public Reservation(ReservationId id, DinnerId dinnerId, GuestId guestId, LocalDateTime reservationDate) {
        this.id = id;
        this.dinnerId = dinnerId;
        this.guestId = guestId;
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public DinnerId getDinnerId() {
        return dinnerId;
    }

    public GuestId getGuestId() {
        return guestId;
    }

    public ReservationId getId() {
        return id;
    }
}

