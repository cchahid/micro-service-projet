package com.buberdinner.NotificationService.infrastructure.clients;

import com.buberdinner.NotificationService.domain.exception.NotificationDeliveryException;
import com.buberdinner.NotificationService.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationSender {

    public void sendPushNotification(Notification notification) {
        // Convert the Long userId to a String
        String userId = String.valueOf(notification.getUserId());
        String subject = notification.getSubject();
        String body = notification.getDescription();

        try {
            log.info("Simulating Push Notification to User ID: {}, Subject: {}, Body: {}", userId, subject, body);
            System.out.println("--- Sending Push Notification ---");
            System.out.println("User ID: " + userId);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("---------------------------------\n");
            log.info("Push notification simulated successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send push notification to user ID {}: {}", userId, e.getMessage(), e);
            throw new NotificationDeliveryException("Push notification delivery failed to user " + userId, e);
        }
    }
}