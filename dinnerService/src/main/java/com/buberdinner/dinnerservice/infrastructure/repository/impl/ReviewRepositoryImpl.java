package com.buberdinner.dinnerservice.infrastructure.repository.impl;

import com.buberdinner.dinnerservice.domain.entity.Review;
import com.buberdinner.dinnerservice.domain.repository.ReviewRepository;
import com.buberdinner.dinnerservice.infrastructure.entity.ReviewEntity;
import com.buberdinner.dinnerservice.infrastructure.repository.ReviewJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review save(Review review) {
        ReviewEntity reviewEntity = ReviewEntity.fromDomain(review);
        ReviewEntity savedEntity = reviewJpaRepository.save(reviewEntity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewJpaRepository.findById(id)
                .map(ReviewEntity::toDomain);
    }

    @Override
    public List<Review> findAll() {
        return reviewJpaRepository.findAll().stream()
                .map(ReviewEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByHostId(Long hostID) {
        return reviewJpaRepository.findByHostId(hostID).stream()
                .map(ReviewEntity::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewJpaRepository.findByUserId(userId).stream()
                .map(ReviewEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        reviewJpaRepository.deleteById(id);
    }
}