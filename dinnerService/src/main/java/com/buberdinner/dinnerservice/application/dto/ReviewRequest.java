package com.buberdinner.dinnerservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for review request in the application layer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private Long dinnerId;
    private Long userId;
    private String comment;
    private int rating;
}