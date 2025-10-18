package com.buberdinner.NotificationService.infrastructure.persistence.entity;

import com.buberdinner.NotificationService.domain.model.NotificationChannel;
import com.buberdinner.NotificationService.domain.model.NotificationStatus;
import com.buberdinner.NotificationService.domain.model.NotificationUserType; // New import
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor @Builder
public class NotificationEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false) // Renamed from username
    private Long userId; // Changed type to Long

    @Enumerated(EnumType.STRING) // New field
    @Column(name = "user_type", nullable = false)
    private NotificationUserType userType;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String subject;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
}