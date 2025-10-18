package com.buberdinner.NotificationService.infrastructure.persistence.mapper;

import com.buberdinner.NotificationService.domain.model.Notification;
import com.buberdinner.NotificationService.domain.model.NotificationUserType; // New import
import com.buberdinner.NotificationService.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
public class NotificationEntityMapper {

    public NotificationEntity toEntity(Notification domain) {
        if (domain == null) {
            return null;
        }
        return NotificationEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId()) // Changed from username(domain.getUserId())
                .userType(domain.getUserType()) // New
                .email(domain.getEmail())
                .subject(domain.getSubject())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .deleteAt(domain.getDeleteAt())
                .channel(domain.getChannel())
                .status(domain.getStatus())
                .build();
    }

    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUserId()) // Changed from userId(entity.getUsername())
                .userType(entity.getUserType()) // New
                .email(entity.getEmail())
                .subject(entity.getSubject())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .deleteAt(entity.getDeleteAt())
                .channel(entity.getChannel())
                .status(entity.getStatus())
                .build();
    }
}