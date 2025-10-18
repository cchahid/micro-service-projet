package com.buberdinner.dinnerservice.infrastructure.entity;

import com.buberdinner.dinnerservice.domain.entity.Dinner;
import com.buberdinner.dinnerservice.domain.valueobject.DinnerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for Dinner.
 */
@Entity
@Table(name = "dinners")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DinnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "host_id")
    private Long hostId;
    @Column(name = "menu_id")
    private Long menuId;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private double price;
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "address")
    private String address;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "max_guest_count")
    private int maxGuestCount;

    @Enumerated(EnumType.STRING)
    private DinnerStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    private MenuEntity menu;

    /**
     * Converts a domain Dinner entity to a JPA DinnerEntity.
     * 
     * @param dinner the domain entity
     * @return the JPA entity
     */
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
        entity.setImageUrl(dinner.getImageUrl());
        return entity;
    }

    /**
     * Converts this JPA entity to a domain Dinner entity.
     * 
     * @return the domain entity
     */
    public Dinner toDomain() {
        Dinner dinner = new Dinner(
                id,
                hostId,
                menuId,
                name,
                description,
                price,
                startTime,
                endTime,
                address,
                cuisineType,
                maxGuestCount,
                imageUrl
        );

        // Set the status if it's not null
        if (status != null && dinner.getStatus() != status) {
            // We need to use reflection or a setter method to set the status
            // since the constructor doesn't accept it directly
            try {
                java.lang.reflect.Field statusField = Dinner.class.getDeclaredField("status");
                statusField.setAccessible(true);
                statusField.set(dinner, status);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set dinner status", e);
            }
        }

        return dinner;
    }
}