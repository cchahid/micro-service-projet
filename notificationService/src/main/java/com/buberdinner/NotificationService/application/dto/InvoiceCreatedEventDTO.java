package com.buberdinner.NotificationService.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO for the FactureCreated event (InvoiceCreatedEventDTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreatedEventDTO {
    private String invoiceId;
    private Long dinnerId; // Changed to Long
    private Long guestId; // Changed to Long, remove guestEmail, guestUsername
    private String amount; // Example: "123.45 MAD"
    private LocalDateTime invoiceDate;
    // Add other relevant fields
}