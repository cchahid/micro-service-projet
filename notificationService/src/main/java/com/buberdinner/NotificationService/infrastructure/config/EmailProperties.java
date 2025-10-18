package com.buberdinner.NotificationService.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor; // Added for builder convenience
import lombok.NoArgsConstructor; // Added for default constructor for Spring Boot
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.mail") // Changed prefix to spring.mail to align with Spring Boot's default
@NoArgsConstructor
@AllArgsConstructor
public class EmailProperties {
    private String host;
    private int port;
    private String username;
    private String password;

    // This method should return the actual sender email, which is often the username
    public String getFrom() {
        return this.username;
    }
}