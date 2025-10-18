package com.buberdinner.dinnerservice.application.service;

import com.buberdinner.dinnerservice.application.dto.ReviewRequest;
import com.buberdinner.dinnerservice.application.dto.ReviewResponse;

import java.util.List;

/**
 * Application service interface for review operations.
 */
public interface ReviewApplicationService {
    /**
     * Creates a new review.
     * 
     * @param reviewRequest the review request
     * @return the created review
     */
    ReviewResponse createReview(ReviewRequest reviewRequest);
    
    /**
     * Gets a review by its ID.
     * 
     * @param id the review ID
     * @return the review
     */
    ReviewResponse getReviewById(Long id);
    
    /**
     * Gets all reviews for a dinner.
     * 
     * @param dinnerId the dinner ID
     * @return the list of reviews
     */
    List<ReviewResponse> getReviewsByDinnerId(Long dinnerId);
    
    /**
     * Gets all reviews by a user.
     * 
     * @param userId the user ID
     * @return the list of reviews
     */
    List<ReviewResponse> getReviewsByUserId(Long userId);
    
    /**
     * Deletes a review.
     * 
     * @param id the review ID
     */
    void deleteReview(Long id);

    List<ReviewResponse> getAllReviews();

    long meanReviewsByDinnerId(Long dinnerId);
}