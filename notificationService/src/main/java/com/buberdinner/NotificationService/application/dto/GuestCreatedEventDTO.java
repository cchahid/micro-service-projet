package com.buberdinner.NotificationService.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestCreatedEventDTO {
    private Long id;
    private String nom; // Name
    private String email;
}