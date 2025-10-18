package com.buberdinner.NotificationService;

import com.buberdinner.NotificationService.domain.model.NotificationChannel;
import com.buberdinner.NotificationService.domain.model.NotificationStatus;
import com.buberdinner.NotificationService.domain.model.NotificationUserType; // New import: Make sure this is present if you use it
import com.buberdinner.NotificationService.infrastructure.persistence.entity.NotificationEntity;
import com.buberdinner.NotificationService.infrastructure.persistence.repository.NotificationJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CommandLineRunner start(NotificationJpaRepository notificationJpaRepository) {
        return args -> {
            log.info("Running CommandLineRunner to populate initial data...");
            notificationJpaRepository.deleteAll();
            log.info("Cleared existing notifications.");

            Random random = new Random();

            for (int i = 0; i < 5; i++) {
                // We no longer have a 'username' field in NotificationEntity.
                // We'll use email and generate a dummy userId for demonstration.
                // In a real app, userId would come from your user management system.
                Long userId = (long) (100 + i); // Generate a simple dummy userId
                String email = "user" + i + "@example.com"; // Use email as primary identifier
                NotificationUserType userType = (i % 2 == 0) ? NotificationUserType.GUEST : NotificationUserType.HOST; // Determine user type

                String subject;
                String description;
                NotificationChannel channel = (random.nextBoolean()) ? NotificationChannel.EMAIL : NotificationChannel.PUSH;
                NotificationStatus status = (i % 3 == 0) ? NotificationStatus.PENDING : NotificationStatus.SENT;

                if (i == 0) {
                    subject = "RESERVATION_CONFIRMATION";
                    description = "Your reservation for dinner is confirmed! Enjoy your evening.";
                } else if (i == 1) {
                    subject = "DINNER_STARTED";
                    description = "Great news! Your dinner at 'The Grand Bistro' has just started. Bon appétit!";
                } else if (i == 2) {
                    subject = "DINNER_ENDED";
                    description = "Hope you enjoyed your meal. Dinner at 'The Grand Bistro' has concluded.";
                } else if (i == 3) {
                    subject = "INVOICE_SENT";
                    description = "Your invoice for the recent dinner is attached to this email. Thank you!";
                } else {
                    subject = "ACCOUNT_UPDATE";
                    description = "Important update regarding your BuberDinner account.";
                }

                NotificationEntity notificationEntity = NotificationEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId) // Use userId instead of username
                        .userType(userType) // Set the user type
                        .email(email)
                        .subject(subject)
                        .description(description)
                        .createdAt(LocalDateTime.now())
                        .channel(channel)
                        .status(status)
                        .build();

                notificationJpaRepository.save(notificationEntity);
                // Log using userId and email, as username is removed
                log.info("Created and saved notification: {} for user ID: {} (Email: {})",
                        notificationEntity.getSubject(), notificationEntity.getUserId(), notificationEntity.getEmail());
            }
            log.info("\nFinished populating initial notifications with sample data.");
        };
    }
}