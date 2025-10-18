package com.buberdinner.NotificationService.application.ports.input;

import com.buberdinner.NotificationService.application.dto.DinnerEndedEventDTO;
import com.buberdinner.NotificationService.application.dto.DinnerStartedEventDTO;
import com.buberdinner.NotificationService.application.dto.InvoiceCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.ReservationCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.ReservationCanceledEventDTO; // New import
import com.buberdinner.NotificationService.application.dto.GuestCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.HostCreatedEventDTO;

public interface NotificationInputPort {
    void handleReservationCreatedEvent(ReservationCreatedEventDTO event);
    void handleDinnerStartedEvent(DinnerStartedEventDTO event);
    void handleDinnerEndedEvent(DinnerEndedEventDTO event);
    void handleInvoiceCreatedEvent(InvoiceCreatedEventDTO event);
    void handleGuestCreatedEvent(GuestCreatedEventDTO event);
    void handleHostCreatedEvent(HostCreatedEventDTO event);
    void handleReservationCanceledEvent(ReservationCanceledEventDTO event); // New method

    // For direct API requests or other immediate notifications
    void sendImmediateNotification(String userId, String email, String subject, String description, String channel);
}