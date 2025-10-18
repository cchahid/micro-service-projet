package com.buberdinner.dinnerservice.presentation.dto;



import java.util.List;

public record MenuResponse(
        Long id,
        Long hostId,
        String name,
        String description,
        String cuisineType,
        boolean isActive

) {}
