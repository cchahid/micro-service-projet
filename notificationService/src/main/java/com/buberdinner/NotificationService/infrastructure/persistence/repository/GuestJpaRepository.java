package com.buberdinner.NotificationService.infrastructure.persistence.repository;

import com.buberdinner.NotificationService.infrastructure.persistence.entity.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Mark this interface as a Spring Data JPA repository
public interface GuestJpaRepository extends JpaRepository<GuestEntity, Long> {

    // Spring Data JPA will automatically provide common CRUD methods (save, findById, findAll, etc.)
    // Additional custom query methods can be defined here if needed
    // For example, you could add a method to find guests by email:
    Optional<GuestEntity> findByEmail(String email);

}