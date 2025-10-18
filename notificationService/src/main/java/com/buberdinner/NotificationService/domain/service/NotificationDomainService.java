package com.buberdinner.NotificationService.domain.service;

import com.buberdinner.NotificationService.domain.model.Notification;
import com.buberdinner.NotificationService.domain.model.NotificationChannel;
import com.buberdinner.NotificationService.domain.model.NotificationStatus;
import com.buberdinner.NotificationService.domain.model.NotificationUserType; // New import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationDomainService {

    public Notification createNewNotification(Long userId, String email, // Changed userId to Long
                                              String subject, String description,
                                              NotificationChannel channel,
                                              NotificationUserType userType) { // New parameter
        return Notification.builder() // Now builder() will be found
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .userType(userType) // Set new field
                .email(email)
                .subject(subject)
                .description(description)
                .createdAt(LocalDateTime.now())
                .channel(channel)
                .status(NotificationStatus.PENDING)
                .build();
    }

    public Notification updateNotificationStatus(Notification notification, NotificationStatus newStatus) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null.");
        }
        if (newStatus == NotificationStatus.SENT) {
            return notification.markAsSent();
        } else if (newStatus == NotificationStatus.FAILED) {
            return notification.markAsFailed();
        }
        return notification;
    }
}