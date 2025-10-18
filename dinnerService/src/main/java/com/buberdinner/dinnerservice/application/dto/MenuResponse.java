package com.buberdinner.dinnerservice.application.dto;


public record MenuResponse(
        Long id,
        Long hostId,
        String name,
        String description,
        String cuisineType,
        boolean isActive


) {
    public MenuResponse(Long id, Long value, String name, String description, String cuisineType) {
        this(id, value, name, description, cuisineType, true);
    }
}

