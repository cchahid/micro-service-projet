package com.buberdinner.dinnerservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for review request in the presentation layer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private Long hostId;
    private Long userId;
    private String comment;
    private int rating;
}