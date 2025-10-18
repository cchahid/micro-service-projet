package com.buberdinner.dinnerservice.infrastructure.repository.impl;

import com.buberdinner.dinnerservice.domain.entity.Dinner;
import com.buberdinner.dinnerservice.domain.repository.DinnerRepository;
import com.buberdinner.dinnerservice.domain.valueobject.DinnerStatus;
import com.buberdinner.dinnerservice.infrastructure.entity.DinnerEntity;
import com.buberdinner.dinnerservice.infrastructure.repository.DinnerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DinnerRepositoryImpl implements DinnerRepository {

    private final DinnerJpaRepository dinnerJpaRepository;

    @Override
    public Dinner save(Dinner dinner) {
        DinnerEntity dinnerEntity = DinnerEntity.fromDomain(dinner);
        DinnerEntity savedEntity = dinnerJpaRepository.save(dinnerEntity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Dinner> findById(Long id) {
        return dinnerJpaRepository.findById(id)
                .map(DinnerEntity::toDomain);
    }

    @Override
    public List<Dinner> findAll() {
        return dinnerJpaRepository.findAll().stream()
                .map(DinnerEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dinner> findByHostId(Long hostId) {
        return dinnerJpaRepository.findByHostId(hostId).stream()
                .map(DinnerEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dinner> findByMenuId(Long menuId) {
        return dinnerJpaRepository.findByMenuId(menuId).stream()
                .map(DinnerEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        dinnerJpaRepository.deleteById(id);
    }

    @Override
    public List<Dinner> findByMenuIdAndStatus(long menuId, DinnerStatus dinnerStatus) {
        return dinnerJpaRepository.findByMenuIdAndStatus(menuId,dinnerStatus).stream()
                .map(DinnerEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dinner> saveAll(List<Dinner> dinners) {
        List<DinnerEntity> dinnersSaved = dinners.stream().map(DinnerEntity::fromDomain).collect(Collectors.toList());
        return dinnerJpaRepository.saveAll(dinnersSaved).stream()
                .map(DinnerEntity::toDomain).collect(Collectors.toList());
    }

    /*@Override
    public List<Dinner> saveAll(List<Dinner> dinners) {
        List<DinnerEntity> dinnerEntities = dinners.stream()
                .map(DinnerEntity::fromDomain)
                .collect(Collectors.toList());

        List<DinnerEntity> savedEntities = dinnerJpaRepository.saveAll(dinnerEntities);

        return savedEntities.stream()
                .map(DinnerEntity::toDomain)
                .collect(Collectors.toList());
    }*/
}
