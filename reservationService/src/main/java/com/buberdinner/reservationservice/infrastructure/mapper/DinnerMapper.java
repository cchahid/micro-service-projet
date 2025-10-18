package com.buberdinner.reservationservice.infrastructure.mapper;


import com.buberdinner.reservationservice.domain.module.Dinner;
import com.buberdinner.reservationservice.infrastructure.entity.DinnerEntity;
import com.buberdinner.reservationservice.presentation.dto.DinnerResponse;
import org.springframework.stereotype.Component;

@Component
public class DinnerMapper {

    public DinnerEntity toEntity(Dinner domain) {
        if (domain == null) {
            return null;
        }

        DinnerEntity entity = new DinnerEntity();
        entity.setId(domain.getId());
        entity.setHostId(domain.getHostId());
        entity.setMenuId(domain.getMenuId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setAddress(domain.getAddress());
        entity.setCuisineType(domain.getCuisineType());
        entity.setMaxGuestCount(domain.getMaxGuestCount());
        entity.setStatus(domain.getStatus());
        entity.setRating(domain.getRating());

        return entity;
    }

    public Dinner toDomain(DinnerEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Dinner(
                entity.getId(),
                entity.getHostId(),
                entity.getMenuId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getAddress(),
                entity.getCuisineType(),
                entity.getMaxGuestCount(),
                entity.getStatus(),
                entity.getRating()
        );
    }

    public Dinner toDomain(DinnerResponse response) {
        if (response == null) {
            return null;
        }

        return new Dinner(
                response.id(),
                response.hostId(),
                response.menuId(),
                response.name(),
                response.description(),
                response.price(),
                response.startTime(),
                response.endTime(),
                response.address(),
                response.cuisineType(),
                response.maxGuestCount(),
                response.status(),
                response.rating()
        );
    }

    public DinnerResponse toResponse(Dinner domain) {
        if (domain == null) {
            return null;
        }

        return new DinnerResponse(
                domain.getId(),
                domain.getHostId(),
                domain.getMenuId(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getStartTime(),
                domain.getEndTime(),
                domain.getAddress(),
                domain.getCuisineType(),
                domain.getMaxGuestCount(),
                domain.getStatus(),
                domain.getRating()
        );
    }
}
