package com.buberdinner.NotificationService.infrastructure.persistence.repository;

import com.buberdinner.NotificationService.infrastructure.persistence.entity.HostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostJpaRepository extends JpaRepository<HostEntity, Long> {
}