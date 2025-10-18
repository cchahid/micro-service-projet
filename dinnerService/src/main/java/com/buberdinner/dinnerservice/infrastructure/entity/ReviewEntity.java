package com.buberdinner.dinnerservice.infrastructure.entity;

import com.buberdinner.dinnerservice.domain.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for Review.
 */
@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Converts a domain Review entity to a JPA ReviewEntity.
     * 
     * @param review the domain entity
     * @return the JPA entity
     */
    public static ReviewEntity fromDomain(Review review) {
        ReviewEntity entity = new ReviewEntity();
        entity.setId(review.getId());
        entity.setHostId(review.getHostId());
        entity.setUserId(review.getUserId());
        entity.setComment(review.getComment());
        entity.setRating(review.getRating());
        entity.setCreatedAt(review.getCreatedAt());
        return entity;
    }

    /**
     * Converts this JPA entity to a domain Review entity.
     * 
     * @return the domain entity
     */
    public Review toDomain() {
        return new Review(
                id,
                hostId,
                userId,
                comment,
                rating
        );
    }
}