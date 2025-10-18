package com.buberdinner.NotificationService.application.ports.output;

import com.buberdinner.NotificationService.domain.model.Notification;

public interface NotificationSenderPort {
    void sendNotification(Notification notification);
}