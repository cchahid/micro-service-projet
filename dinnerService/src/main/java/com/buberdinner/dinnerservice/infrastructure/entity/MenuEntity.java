package com.buberdinner.dinnerservice.infrastructure.entity;


import com.buberdinner.dinnerservice.domain.entity.Menu;
import com.buberdinner.dinnerservice.domain.valueobject.MenuStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JPA entity for Menu.
 */
@Entity
@Table(name = "menus")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String cuisineType;

    @Column(nullable = false)
    private String active;



    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DinnerEntity> dinners = new ArrayList<>();

    /**
     * Converts a domain Menu entity to a JPA MenuEntity.
     *
     * @param menu the domain entity
     * @return the JPA entity
     */
    public static MenuEntity fromDomain(com.buberdinner.dinnerservice.domain.entity.Menu menu) {
        MenuEntity entity = new MenuEntity();
        entity.setId(menu.getId() != null ? menu.getId(): null);
        entity.setHostId(menu.getHostId().getValue());
        entity.setName(menu.getName());
        entity.setDescription(menu.getDescription());
        entity.setCuisineType(menu.getCuisineType());
        entity.setActive(menu.getStatus().name());
        // Note: Les dinners seront gérés séparément via la relation JPA
        return entity;
    }

    /**
     * Converts this JPA entity to a domain Menu entity.
     *
     * @return the domain entity
     */
    public Menu toDomain() {
        MenuStatus status = active.equals("ACTIVE")? MenuStatus.ACTIVE : MenuStatus.INACTIVE;
        Menu menu = new Menu(
                id,
                hostId,
                name,
                description,
                cuisineType,
                status
        );

        return menu;
    }


}
