package com.buberdinner.dinnerservice.infrastructure.repository;

import com.buberdinner.dinnerservice.infrastructure.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, Long> {
    List<MenuEntity> findByHostId(Long hostId);
}
