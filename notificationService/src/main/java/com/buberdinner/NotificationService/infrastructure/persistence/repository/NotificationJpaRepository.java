package com.buberdinner.NotificationService.infrastructure.persistence.repository;

import com.buberdinner.NotificationService.domain.model.NotificationStatus;
import com.buberdinner.NotificationService.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, String> {
    // Custom query to find notifications by status, ordered by creation time for fair processing
    List<NotificationEntity> findByStatusOrderByCreatedAtAsc(NotificationStatus status);
}