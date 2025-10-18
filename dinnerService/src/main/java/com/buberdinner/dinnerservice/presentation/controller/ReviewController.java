package com.buberdinner.dinnerservice.presentation.controller;

import com.buberdinner.dinnerservice.presentation.dto.ReviewRequest;
import com.buberdinner.dinnerservice.presentation.dto.ReviewResponse;
import com.buberdinner.dinnerservice.application.service.ReviewApplicationService;
import com.buberdinner.dinnerservice.presentation.mapper.ReviewDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewApplicationService reviewApplicationService;
    private final ReviewDtoMapper reviewDtoMapper;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest reviewRequest) {
        com.buberdinner.dinnerservice.application.dto.ReviewRequest applicationRequest = reviewDtoMapper.toApplicationRequest(reviewRequest);
        com.buberdinner.dinnerservice.application.dto.ReviewResponse applicationResponse = reviewApplicationService.createReview(applicationRequest);
        ReviewResponse presentationResponse = reviewDtoMapper.toPresentationResponse(applicationResponse);
        return new ResponseEntity<>(presentationResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        com.buberdinner.dinnerservice.application.dto.ReviewResponse applicationResponse = reviewApplicationService.getReviewById(id);
        ReviewResponse presentationResponse = reviewDtoMapper.toPresentationResponse(applicationResponse);
        return ResponseEntity.ok(presentationResponse);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews(){
        List<com.buberdinner.dinnerservice.application.dto.ReviewResponse> applicationResponses = reviewApplicationService.getAllReviews();
        List<ReviewResponse> presentationResponses = reviewDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }

    @GetMapping("/dinner/{dinnerId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByDinnerId(@PathVariable Long dinnerId) {
        List<com.buberdinner.dinnerservice.application.dto.ReviewResponse> applicationResponses = reviewApplicationService.getReviewsByDinnerId(dinnerId);
        List<ReviewResponse> presentationResponses = reviewDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUserId(@PathVariable Long userId) {
        List<com.buberdinner.dinnerservice.application.dto.ReviewResponse> applicationResponses = reviewApplicationService.getReviewsByUserId(userId);
        List<ReviewResponse> presentationResponses = reviewDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewApplicationService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}