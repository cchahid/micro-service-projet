package com.buberdinner.NotificationService.application.ports.output;

import com.buberdinner.NotificationService.domain.model.Notification;

import java.util.List;

public interface NotificationPersistencePort {
    Notification save(Notification notification);
    List<Notification> findPendingNotifications();
    Notification findById(String id);
}