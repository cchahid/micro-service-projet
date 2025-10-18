package com.buberdinner.dinnerservice.infrastructure.repository;

import com.buberdinner.dinnerservice.domain.valueobject.DinnerStatus;
import com.buberdinner.dinnerservice.infrastructure.entity.DinnerEntity;
import com.buberdinner.dinnerservice.infrastructure.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DinnerJpaRepository extends JpaRepository<DinnerEntity, Long> {
    List<DinnerEntity> findByHostId(Long hostId);
    List<DinnerEntity> findByMenuId(Long menuId);
    List<DinnerEntity> findByMenuIdAndStatus(Long menuId, DinnerStatus status);


}
