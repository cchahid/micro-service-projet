package com.buberdinner.NotificationService.infrastructure.persistence.mapper;

import com.buberdinner.NotificationService.domain.model.Host;
import com.buberdinner.NotificationService.infrastructure.persistence.entity.HostEntity;
import org.springframework.stereotype.Component;

@Component
public class HostEntityMapper {
    public HostEntity toEntity(Host domain) {
        if (domain == null) {
            return null;
        }
        return HostEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .build();
    }

    public Host toDomain(HostEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Host(
                entity.getId(),
                entity.getName(),
                entity.getEmail()
        );
    }
}