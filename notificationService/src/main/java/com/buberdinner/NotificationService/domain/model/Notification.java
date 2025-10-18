package com.buberdinner.NotificationService.domain.model;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;

@Value
@Builder // Ensure @Builder is here to generate the builder() method
public class Notification {
    String id;
    Long userId; // Changed from String to Long
    NotificationUserType userType; // New field to distinguish Guest/Host
    String email;
    String subject;
    String description;
    LocalDateTime createdAt;
    LocalDateTime deleteAt;
    NotificationChannel channel;
    NotificationStatus status;

    public Notification markAsSent() {
        return Notification.builder() // Now builder() will be found
                .id(this.id)
                .userId(this.userId)
                .userType(this.userType) // Include new field
                .email(this.email)
                .subject(this.subject)
                .description(this.description)
                .createdAt(this.createdAt)
                .deleteAt(this.deleteAt)
                .channel(this.channel)
                .status(NotificationStatus.SENT)
                .build();
    }
    public Notification markAsFailed() {
        return Notification.builder() // Now builder() will be found
                .id(this.id)
                .userId(this.userId)
                .userType(this.userType) // Include new field
                .email(this.email)
                .subject(this.subject)
                .description(this.description)
                .createdAt(this.createdAt)
                .deleteAt(this.deleteAt)
                .channel(this.channel)
                .status(NotificationStatus.FAILED)
                .build();
    }

    public boolean isReadyToSend() {
        return email != null && !email.isEmpty() && status == NotificationStatus.PENDING;
    }
}