package com.buberdinner.dinnerservice.application.service.impl;

import com.buberdinner.dinnerservice.application.dto.ReviewRequest;
import com.buberdinner.dinnerservice.application.dto.ReviewResponse;
import com.buberdinner.dinnerservice.application.service.ReviewApplicationService;
import com.buberdinner.dinnerservice.domain.entity.Review;
import com.buberdinner.dinnerservice.domain.repository.DinnerRepository;
import com.buberdinner.dinnerservice.domain.repository.ReviewRepository;
import com.buberdinner.dinnerservice.infrastructure.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewApplicationServiceImpl implements ReviewApplicationService {

    private final ReviewRepository reviewRepository;
    private final DinnerRepository dinnerRepository;
    private final UserServiceClient userServiceClient;


    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        // Validate dinner ID
        if (!dinnerRepository.findById(reviewRequest.getDinnerId()).isPresent()) {
            throw new IllegalArgumentException("Invalid dinner ID: Dinner does not exist");
        }

        // Validate user ID
        if (!userServiceClient.userExists(reviewRequest.getUserId())) {
            throw new IllegalArgumentException("Invalid user ID: User does not exist");
        }

        Review review = mapToReview(reviewRequest);

        if (!review.isValid()) {
            throw new IllegalArgumentException("Invalid review: " + String.join(", ", review.getErrors()));
        }

        Review savedReview = reviewRepository.save(review);
        log.info("Saved review: {}", savedReview);
        return mapToReviewResponse(savedReview);
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        return mapToReviewResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByDinnerId(Long hostId) {
        List<Review> reviews = reviewRepository.findByHostId(hostId);
        return reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(Long id) {
        if (!reviewRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long meanReviewsByDinnerId(Long hostId) {
        List<Review> reviews = reviewRepository.findByHostId(hostId);
        if (reviews.isEmpty() || reviews.size() < 10) {return 0;}
        int sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum/reviews.size() ;
    }

    private Review mapToReview(ReviewRequest reviewRequest) {
        return new Review(
                null,
                reviewRequest.getDinnerId(),
                reviewRequest.getUserId(),
                reviewRequest.getComment(),
                reviewRequest.getRating()
        );
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getHostId(),
                review.getUserId(),
                review.getComment(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}