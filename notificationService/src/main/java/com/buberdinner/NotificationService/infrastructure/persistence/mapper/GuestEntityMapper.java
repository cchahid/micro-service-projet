package com.buberdinner.NotificationService.infrastructure.persistence.mapper;

import com.buberdinner.NotificationService.domain.model.Guest;
import com.buberdinner.NotificationService.infrastructure.persistence.entity.GuestEntity;
import org.springframework.stereotype.Component;

@Component
public class GuestEntityMapper {
    public GuestEntity toEntity(Guest domain) {
        if (domain == null) {
            return null;
        }
        return GuestEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .build();
    }

    public Guest toDomain(GuestEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Guest(
                entity.getId(),
                entity.getName(),
                entity.getEmail()
        );
    }
}