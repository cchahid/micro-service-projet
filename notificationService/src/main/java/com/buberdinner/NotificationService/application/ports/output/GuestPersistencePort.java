package com.buberdinner.NotificationService.application.ports.output;

import com.buberdinner.NotificationService.domain.model.Guest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface GuestPersistencePort {
    Guest save(Guest guest);
    Optional<Guest> findById(Long id);
}