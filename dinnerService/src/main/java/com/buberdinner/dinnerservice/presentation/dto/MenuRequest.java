package com.buberdinner.dinnerservice.presentation.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuRequest(
        @NotNull(message = "Host ID is required")
        Long hostId,

        @NotBlank(message = "Menu name is required")
        String name,

        String description,

        @NotBlank(message = "Cuisine type is required")
        String cuisineType,

        boolean isActive
) {}