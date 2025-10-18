package com.buberdinner.dinnerservice.domain.entity;

;
import com.buberdinner.dinnerservice.domain.valueobject.HostId;
import com.buberdinner.dinnerservice.domain.valueobject.MenuStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Menu {
    private Long id;
    private HostId hostId;
    private String name;
    private String description;
    private String cuisineType;
    private MenuStatus status;


    private final List<String> errors = new ArrayList<>();

    public Menu(Long id, long hostId, String name, String description,
                String cuisineType, MenuStatus status) {
        this.id = id;
        try {
            this.hostId = HostId.of(hostId);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid host ID: " + e.getMessage());
        }
        this.name = name;
        this.description = description;
        this.cuisineType = cuisineType;
        this.status =  status;
        validate();
    }





    public void activate() {
        this.status = MenuStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = MenuStatus.INACTIVE;
    }



    public boolean isValid() {
        validate();
        return errors.isEmpty();
    }

    private void validate() {
        errors.clear();
        if (hostId == null || hostId.getValue() <= 0) {
            errors.add("Invalid host ID");
        }
        if (name == null || name.trim().isEmpty()) {
            errors.add("Menu name is required");
        }
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            errors.add("Cuisine type is required");
        }
    }
}