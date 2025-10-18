package com.buberdinner.reservationservice.application.service;

import com.buberdinner.reservationservice.domain.module.Dinner;

import java.util.Optional;

public interface DinnerService {
     Dinner createOrUpdateDinner(Dinner dinner);
    Optional<Dinner> findById(Long id);
}
