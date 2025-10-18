package com.buberdinner.NotificationService.infrastructure.persistence.adapter;

import com.buberdinner.NotificationService.application.ports.output.NotificationPersistencePort;
import com.buberdinner.NotificationService.domain.model.Notification;
import com.buberdinner.NotificationService.domain.model.NotificationStatus;
import com.buberdinner.NotificationService.infrastructure.persistence.entity.NotificationEntity;
import com.buberdinner.NotificationService.infrastructure.persistence.mapper.NotificationEntityMapper; // Corrected mapper import
import com.buberdinner.NotificationService.infrastructure.persistence.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationPersistencePort {

    private final NotificationJpaRepository repository;
    private final NotificationEntityMapper mapper; // Renamed for clarity from 'mapper' to 'notificationEntityMapper' if desired

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = mapper.toEntity(notification);
        NotificationEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Notification> findPendingNotifications() {
        return repository.findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Notification findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElse(null); // Or throw a NotFoundException
    }
}