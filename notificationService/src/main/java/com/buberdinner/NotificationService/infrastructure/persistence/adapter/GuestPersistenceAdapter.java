package com.buberdinner.NotificationService.infrastructure.persistence.adapter;

import com.buberdinner.NotificationService.application.ports.output.GuestPersistencePort;
import com.buberdinner.NotificationService.domain.model.Guest;
import com.buberdinner.NotificationService.infrastructure.persistence.entity.GuestEntity;
import com.buberdinner.NotificationService.infrastructure.persistence.repository.GuestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository; // Best practice for persistence layer

import java.util.Optional;

@Repository // Makes this class a Spring bean and indicates it's a data access object
@RequiredArgsConstructor // Lombok generates constructor for final fields
public class GuestPersistenceAdapter implements GuestPersistencePort {

    private final GuestJpaRepository guestJpaRepository;

    @Override
    public Guest save(Guest guest) {
        // Convert domain model Guest to JPA entity GuestEntity
        GuestEntity guestEntity = toEntity(guest);
        GuestEntity savedEntity = guestJpaRepository.save(guestEntity);
        // Convert saved JPA entity back to domain model Guest
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Guest> findById(Long id) {
        // Find by ID and convert the Optional<GuestEntity> to Optional<Guest>
        return guestJpaRepository.findById(id)
                .map(this::toDomain); // Use map to convert if present
    }

    // --- Helper methods for conversion between Domain Model and Entity ---

    private Guest toDomain(GuestEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Guest(entity.getId(), entity.getName(), entity.getEmail());
    }

    private GuestEntity toEntity(Guest domain) {
        if (domain == null) {
            return null;
        }
        // Assuming GuestEntity has an all-args constructor or you can use a builder if available
        return new GuestEntity(domain.getId(), domain.getName(), domain.getEmail());
    }
}