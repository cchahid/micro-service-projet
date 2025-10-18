package com.buberdinner.reservationservice.application.service.impl;



import com.buberdinner.reservationservice.application.service.DinnerService;
import com.buberdinner.reservationservice.domain.module.Dinner;
import com.buberdinner.reservationservice.infrastructure.repository.DinnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DinnerServiceImpl implements DinnerService {
    private final DinnerRepository dinnerRepository;



    public DinnerServiceImpl(DinnerRepository dinnerRepository) {
        this.dinnerRepository = dinnerRepository;
    }

    @Transactional
    public Dinner createOrUpdateDinner(Dinner dinner) {
        return dinnerRepository.save(dinner);
    }

    @Transactional(readOnly = true)
    public Optional<Dinner> findById(Long id) {
        return dinnerRepository.findByIdDinner(id);
    }




}
