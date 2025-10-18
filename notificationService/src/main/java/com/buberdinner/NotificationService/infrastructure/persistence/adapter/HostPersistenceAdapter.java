package com.buberdinner.NotificationService.infrastructure.persistence.adapter;

import com.buberdinner.NotificationService.application.ports.output.HostPersistencePort;
import com.buberdinner.NotificationService.domain.model.Host;
import com.buberdinner.NotificationService.infrastructure.persistence.entity.HostEntity;
import com.buberdinner.NotificationService.infrastructure.persistence.repository.HostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HostPersistenceAdapter implements HostPersistencePort {

    private final HostJpaRepository hostJpaRepository;

    @Override
    public Host save(Host host) {
        HostEntity hostEntity = toEntity(host);
        HostEntity savedEntity = hostJpaRepository.save(hostEntity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Host> findById(Long id) {
        return hostJpaRepository.findById(id)
                .map(this::toDomain);
    }

    // --- Helper methods for conversion between Domain Model and Entity ---

    private Host toDomain(HostEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Host(entity.getId(), entity.getName(), entity.getEmail());
    }

    private HostEntity toEntity(Host domain) {
        if (domain == null) {
            return null;
        }
        return new HostEntity(domain.getId(), domain.getName(), domain.getEmail());
    }
}