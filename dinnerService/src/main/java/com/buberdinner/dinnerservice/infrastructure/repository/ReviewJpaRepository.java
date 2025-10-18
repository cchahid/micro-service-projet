package com.buberdinner.dinnerservice.infrastructure.repository;

import com.buberdinner.dinnerservice.infrastructure.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByUserId(Long userId);

    List<ReviewEntity> findByHostId(Long hostId);
}