package com.buberdinner.NotificationService.application.ports.output;

import com.buberdinner.NotificationService.domain.model.Host;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface HostPersistencePort {
    Host save(Host host);
    Optional<Host> findById(Long id);
}