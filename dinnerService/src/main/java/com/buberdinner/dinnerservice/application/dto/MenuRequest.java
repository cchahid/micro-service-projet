package com.buberdinner.dinnerservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuRequest(
        @NotNull Long hostId,
        @NotBlank String name,
        String description,
        @NotBlank String cuisineType,
        boolean isActive
) {}

