package com.buberdinner.reservationservice.infrastructure.entity;


import com.buberdinner.reservationservice.domain.module.Dinner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dinners")
@Getter
@Setter
@NoArgsConstructor
public class DinnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "menu_id")
    private Long menuId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String address;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "max_guest_count", nullable = false)
    private int maxGuestCount;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private long rating = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static DinnerEntity fromDomain(Dinner dinner) {
        DinnerEntity entity = new DinnerEntity();
        entity.setId(dinner.getId());
        entity.setHostId(dinner.getHostId());
        entity.setMenuId(dinner.getMenuId());
        entity.setName(dinner.getName());
        entity.setDescription(dinner.getDescription());
        entity.setPrice(dinner.getPrice());
        entity.setStartTime(dinner.getStartTime());
        entity.setEndTime(dinner.getEndTime());
        entity.setAddress(dinner.getAddress());
        entity.setCuisineType(dinner.getCuisineType());
        entity.setMaxGuestCount(dinner.getMaxGuestCount());
        entity.setStatus(dinner.getStatus());
        entity.setRating(dinner.getRating());
        return entity;
    }

    public Dinner toDomain() {
        return new Dinner(
                this.id,
                this.hostId,
                this.menuId,
                this.name,
                this.description,
                this.price,
                this.startTime,
                this.endTime,
                this.address,
                this.cuisineType,
                this.maxGuestCount,
                this.status,
                this.rating
        );
    }
}