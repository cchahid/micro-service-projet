package com.buberdinner.dinnerservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for review response in the application layer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long dinnerId;
    private Long userId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
}