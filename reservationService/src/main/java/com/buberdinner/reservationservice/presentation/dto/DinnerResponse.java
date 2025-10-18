package com.buberdinner.reservationservice.presentation.dto;

import java.time.LocalDateTime;

public record DinnerResponse(
        Long id,
        Long hostId,
        Long menuId,
        String name,
        String description,
        double price,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String address,
        String cuisineType,
        int maxGuestCount,
        String status,
        long rating
) {
//    // Vous pouvez ajouter un factory method pour créer à partir d'un Dinner simplifié
//    public static DinnerResponse fromBasicInfo(Long id, Long hostId, String name, String address) {
//        return new DinnerResponse(
//                id, hostId, name, null, 0.0,
//                null, null, address, null, 0, "ACTIVE", 0
//        );
//    }
}
