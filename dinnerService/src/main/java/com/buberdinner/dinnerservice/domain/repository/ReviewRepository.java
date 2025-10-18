package com.buberdinner.dinnerservice.domain.repository;

import com.buberdinner.dinnerservice.domain.entity.Review;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity.
 */
public interface ReviewRepository {
    /**
     * Saves a review entity.
     * 
     * @param review the review to save
     * @return the saved review
     */
    Review save(Review review);

    /**
     * Finds a review by its ID.
     * 
     * @param id the review ID
     * @return an Optional containing the review if found, or empty if not found
     */
    Optional<Review> findById(Long id);

    /**
     * Finds all reviews.
     * 
     * @return a list of all reviews
     */
    List<Review> findAll();

    /**
     * Finds all reviews by dinner ID.
     * 
     * @param hostID the dinner ID
     * @return a list of reviews for the given dinner
     */
    List<Review> findByHostId(Long hostID);

    /**
     * Finds all reviews by user ID.
     * 
     * @param userId the user ID
     * @return a list of reviews by the given user
     */
    List<Review> findByUserId(Long userId);

    /**
     * Deletes a review by its ID.
     * 
     * @param id the review ID
     */
    void deleteById(Long id);
}