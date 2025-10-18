package com.buberdinner.NotificationService.application.service;

import com.buberdinner.NotificationService.application.dto.DinnerEndedEventDTO;
import com.buberdinner.NotificationService.application.dto.DinnerStartedEventDTO;
import com.buberdinner.NotificationService.application.dto.InvoiceCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.ReservationCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.ReservationCanceledEventDTO;
import com.buberdinner.NotificationService.application.dto.GuestCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.HostCreatedEventDTO;
import com.buberdinner.NotificationService.application.ports.input.NotificationInputPort;
import com.buberdinner.NotificationService.application.ports.output.NotificationPersistencePort;
import com.buberdinner.NotificationService.application.ports.output.NotificationSenderPort;
import com.buberdinner.NotificationService.application.ports.output.GuestPersistencePort;
import com.buberdinner.NotificationService.application.ports.output.HostPersistencePort;
import com.buberdinner.NotificationService.domain.exception.NotificationDeliveryException;
import com.buberdinner.NotificationService.domain.model.Notification;
import com.buberdinner.NotificationService.domain.model.NotificationChannel;
import com.buberdinner.NotificationService.domain.model.NotificationStatus;
import com.buberdinner.NotificationService.domain.model.NotificationUserType;
import com.buberdinner.NotificationService.domain.model.Guest;
import com.buberdinner.NotificationService.domain.model.Host;
import com.buberdinner.NotificationService.domain.service.NotificationDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationApplicationService implements NotificationInputPort {

    private final NotificationPersistencePort notificationPersistencePort;
    private final NotificationSenderPort notificationSenderPort;
    private final NotificationDomainService notificationDomainService;
    private final GuestPersistencePort guestPersistencePort;
    private final HostPersistencePort hostPersistencePort;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd,yyyy 'at' hh:mm a");

    @Override
    @Transactional
    public void handleReservationCreatedEvent(ReservationCreatedEventDTO event) {
        Guest guest = guestPersistencePort.findById(event.getGuestId())
                .orElseThrow(() -> new IllegalArgumentException("Guest not found for ID: " + event.getGuestId()));

        log.info("Handling ReservationCreatedEvent for guest: {}", guest.getEmail());
        String subject = "Reservation Confirmation";
        String description = String.format("Dear %s, your reservation at %s on %s is confirmed. Reservation ID: %s",
                guest.getName(), event.getRestaurantName(), event.getReservationTime().format(DATE_TIME_FORMATTER), event.getReservationId());

        Notification notification = notificationDomainService.createNewNotification(
                guest.getId(),
                guest.getEmail(),
                subject,
                description,
                NotificationChannel.EMAIL,
                NotificationUserType.GUEST
        );
        notificationPersistencePort.save(notification);
        sendAndMarkNotification(notification);
    }

    @Override
    @Transactional
    public void handleReservationCanceledEvent(ReservationCanceledEventDTO event) {
        Guest guest = guestPersistencePort.findById(event.getGuestId())
                .orElseThrow(() -> new IllegalArgumentException("Guest not found for ID: " + event.getGuestId()));

        log.info("Handling ReservationCanceledEvent for guest: {}", guest.getEmail());
        String subject = "Reservation Canceled";
        String description = String.format("Dear %s, your reservation with ID %s has been successfully canceled.",
                guest.getName(), event.getReservationId());

        Notification notification = notificationDomainService.createNewNotification(
                guest.getId(),
                guest.getEmail(),
                subject,
                description,
                NotificationChannel.EMAIL,
                NotificationUserType.GUEST
        );
        notificationPersistencePort.save(notification);
        sendAndMarkNotification(notification);
    }

    @Override
    @Transactional
    public void handleDinnerStartedEvent(DinnerStartedEventDTO event) {
        // Iterate through all guest IDs in the event
        if (event.getGuestIds() != null && !event.getGuestIds().isEmpty()) {
            for (Long guestId : event.getGuestIds()) {
                guestPersistencePort.findById(guestId).ifPresentOrElse(
                        guest -> {
                            log.info("Handling DinnerStartedEvent for guest: {} (ID: {})", guest.getEmail(), guestId);
                            String subject = "Dinner Has Started!";
                            String description = String.format("Dear %s, your dinner at %s has just begun. Enjoy your meal!",
                                    guest.getName(), event.getRestaurantName());

                            Notification notification = notificationDomainService.createNewNotification(
                                    guest.getId(),
                                    guest.getEmail(),
                                    subject,
                                    description,
                                    NotificationChannel.IN_APP, // Assuming in-app notification for dinner started
                                    NotificationUserType.GUEST
                            );
                            notificationPersistencePort.save(notification);
                            // Optionally send immediately or let the scheduler pick it up
                            // sendAndMarkNotification(notification);
                        },
                        () -> log.warn("Guest not found for ID {} when handling DinnerStartedEvent. Skipping notification.", guestId)
                );
            }
        } else {
            log.warn("DinnerStartedEvent received for dinner ID {} with no guest IDs.", event.getDinnerId());
        }
    }

    @Override
    @Transactional
    public void handleDinnerEndedEvent(DinnerEndedEventDTO event) {
        // Iterate through all guest IDs in the event
        if (event.getGuestIds() != null && !event.getGuestIds().isEmpty()) {
            for (Long guestId : event.getGuestIds()) {
                guestPersistencePort.findById(guestId).ifPresentOrElse(
                        guest -> {
                            log.info("Handling DinnerEndedEvent for guest: {} (ID: {})", guest.getEmail(), guestId);
                            String subject = "Dinner Concluded";
                            String description = String.format("Dear %s, your dinner at %s has concluded. We hope you had a wonderful time!",
                                    guest.getName(), event.getRestaurantName());

                            Notification notification = notificationDomainService.createNewNotification(
                                    guest.getId(),
                                    guest.getEmail(),
                                    subject,
                                    description,
                                    NotificationChannel.IN_APP, // Assuming in-app notification for dinner ended
                                    NotificationUserType.GUEST
                            );
                            notificationPersistencePort.save(notification);
                            // Optionally send immediately or let the scheduler pick it up
                            // sendAndMarkNotification(notification);
                        },
                        () -> log.warn("Guest not found for ID {} when handling DinnerEndedEvent. Skipping notification.", guestId)
                );
            }
        } else {
            log.warn("DinnerEndedEvent received for dinner ID {} with no guest IDs.", event.getDinnerId());
        }
    }

    @Override
    @Transactional
    public void handleInvoiceCreatedEvent(InvoiceCreatedEventDTO event) {
        Guest guest = guestPersistencePort.findById(event.getGuestId())
                .orElseThrow(() -> new IllegalArgumentException("Guest not found for ID: " + event.getGuestId()));

        log.info("Handling InvoiceCreatedEvent for guest: {}", guest.getEmail());
        String subject = "Your Dinner Invoice";
        String description = String.format("Dear %s, your invoice for the recent dinner on %s is ready. Amount: %s. Invoice ID: %s",
                guest.getName(), event.getInvoiceDate().toLocalDate(), event.getAmount(), event.getInvoiceId());

        Notification notification = notificationDomainService.createNewNotification(
                guest.getId(),
                guest.getEmail(),
                subject,
                description,
                NotificationChannel.EMAIL,
                NotificationUserType.GUEST
        );
        notificationPersistencePort.save(notification);
        sendAndMarkNotification(notification);
    }

    @Override
    @Transactional
    public void sendImmediateNotification(String userIdString, String email, String subject, String description, String channelString) {
        log.info("Sending immediate notification to {} via {}", email, channelString);
        try {
            Long userId = Long.parseLong(userIdString);
            NotificationChannel channel = NotificationChannel.valueOf(channelString.toUpperCase());

            NotificationUserType userType = NotificationUserType.UNKNOWN;
            if (guestPersistencePort.findById(userId).isPresent()) {
                userType = NotificationUserType.GUEST;
            } else if (hostPersistencePort.findById(userId).isPresent()) {
                userType = NotificationUserType.HOST;
            }

            Notification notification = notificationDomainService.createNewNotification(
                    userId, email, subject, description, channel, userType
            );
            notificationPersistencePort.save(notification);
            sendAndMarkNotification(notification);
        } catch (IllegalArgumentException e) {
            log.error("Invalid notification channel or user ID format specified: {}", channelString, e);
            throw new IllegalArgumentException("Invalid notification channel or user ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to send immediate notification to {}: {}", email, e.getMessage(), e);
            throw new NotificationDeliveryException("Failed to send immediate notification to " + email, e);
        }
    }

    @Override
    @Transactional
    public void handleGuestCreatedEvent(GuestCreatedEventDTO event) {
        log.info("Handling GuestCreatedEvent for guest: {} ({})", event.getNom(), event.getEmail());
        Guest guest = new Guest(event.getId(), event.getNom(), event.getEmail());
        guestPersistencePort.save(guest);
        log.info("Saved guest locally: {}", guest);
    }

    @Override
    @Transactional
    public void handleHostCreatedEvent(HostCreatedEventDTO event) {
        log.info("Handling HostCreatedEvent for host: {} ({})", event.getNom(), event.getEmail());
        Host host = new Host(event.getId(), event.getNom(), event.getEmail());
        hostPersistencePort.save(host);
        log.info("Saved host locally: {}", host);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    @Transactional
    public void processPendingNotifications() {
        log.info("Scheduler: Checking for pending notifications at {}", LocalDateTime.now());
        List<Notification> pendingNotifications = notificationPersistencePort.findPendingNotifications();

        if (pendingNotifications.isEmpty()) {
            log.info("Scheduler: No pending notifications found.");
            return;
        }

        for (Notification notification : pendingNotifications) {
            sendAndMarkNotification(notification);
        }
    }

    private void sendAndMarkNotification(Notification notification) {
        try {
            if (notification.isReadyToSend()) {
                notificationSenderPort.sendNotification(notification);
                Notification updatedNotification = notificationDomainService.updateNotificationStatus(notification, NotificationStatus.SENT);
                notificationPersistencePort.save(updatedNotification);
                log.info("Successfully sent notification {} (ID: {}) for {}", notification.getSubject(), notification.getId(), notification.getEmail());
            } else {
                log.warn("Notification {} (ID: {}) is not ready to send. Status: {}, Email empty: {}",
                        notification.getSubject(), notification.getId(), notification.getStatus(), notification.getEmail() == null || notification.getEmail().isEmpty());
                Notification failedNotification = notificationDomainService.updateNotificationStatus(notification, NotificationStatus.FAILED);
                notificationPersistencePort.save(failedNotification);
            }
        } catch (Exception e) {
            log.error("Failed to send notification {} (ID: {}) to {}: {}",
                    notification.getSubject(), notification.getId(), notification.getEmail(), e.getMessage(), e);
            Notification failedNotification = notificationDomainService.updateNotificationStatus(notification, NotificationStatus.FAILED);
            notificationPersistencePort.save(failedNotification);
        }
    }
}