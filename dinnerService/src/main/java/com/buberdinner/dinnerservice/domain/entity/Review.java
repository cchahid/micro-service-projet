package com.buberdinner.dinnerservice.domain.entity;

import com.buberdinner.dinnerservice.domain.valueobject.HostId;
import com.buberdinner.dinnerservice.domain.valueobject.UserId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing a review for a dinner.
 */
@Getter
public class Review {
    private Long id;
    private HostId hostId;
    private UserId userId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
    
    private final List<String> errors = new ArrayList<>();
    
    /**
     * Default constructor for JPA.
     */
    protected Review() {
        // Required by JPA
    }
    
    /**
     * Creates a new review with the given parameters.
     */
    public Review(Long id, Long hostId, Long userId, String comment, int rating) {
        this.id = id;
        
        try {
            this.hostId = HostId.of(hostId);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid dinner ID: " + e.getMessage());
        }
        
        try {
            this.userId = UserId.of(userId);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid user ID: " + e.getMessage());
        }
        
        this.comment = comment;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
        
        validate();
    }
    
    /**
     * Gets the Host ID as a primitive long.
     *
     * @return the Host ID value
     */
    public Long getHostId() {
        return hostId != null ? hostId.getValue() : null;
    }
    
    /**
     * Gets the user ID as a primitive long.
     *
     * @return the user ID value
     */
    public Long getUserId() {
        return userId != null ? userId.getValue() : null;
    }
    
    /**
     * Validates the review entity.
     * Adds validation errors to the errors list.
     */
    private void validate() {
        errors.clear();
        
        if (hostId == null) {
            errors.add("Host ID is required");
        }
        
        if (userId == null) {
            errors.add("User ID is required");
        }
        
        if (comment == null || comment.trim().isEmpty()) {
            errors.add("Comment is required");
        }
        
        if (rating < 1 || rating > 5) {
            errors.add("Rating must be between 1 and 5");
        }
    }
    
    /**
     * Checks if the review is valid.
     *
     * @return true if the review is valid, false otherwise
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    /**
     * Gets the validation errors.
     *
     * @return the list of validation errors
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
}