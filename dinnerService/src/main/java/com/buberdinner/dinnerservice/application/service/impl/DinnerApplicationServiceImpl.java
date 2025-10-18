package com.buberdinner.dinnerservice.application.service.impl;

import com.buberdinner.dinnerservice.application.dto.DinnerRequest;
import com.buberdinner.dinnerservice.application.dto.DinnerResponse;
import com.buberdinner.dinnerservice.application.dto.GuestIdListResponse;
import com.buberdinner.dinnerservice.application.service.DinnerApplicationService;
import com.buberdinner.dinnerservice.application.service.ReviewApplicationService;
import com.buberdinner.dinnerservice.domain.entity.Dinner;
import com.buberdinner.dinnerservice.domain.event.DinnerCreatedEvent;
import com.buberdinner.dinnerservice.domain.event.DinnerStartedEvent;
import com.buberdinner.dinnerservice.domain.event.DinnerUpdatedEvent;
import com.buberdinner.dinnerservice.domain.exception.DinnerNotFoundException;
import com.buberdinner.dinnerservice.domain.repository.DinnerRepository;
import com.buberdinner.dinnerservice.domain.valueobject.DinnerStatus;
import com.buberdinner.dinnerservice.infrastructure.client.MenuServiceClient;
import com.buberdinner.dinnerservice.infrastructure.client.UserServiceClient;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DinnerApplicationServiceImpl implements DinnerApplicationService {

    private final DinnerRepository dinnerRepository;
    private final UserServiceClient userServiceClient;
    private final MenuServiceClient menuServiceClient;
    private final ReviewApplicationService reviewApplicationService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RestTemplate restTemplate;


    private String reservationServiceUrl="http://reservationService";

    @Override
    @Transactional
    public DinnerResponse createDinner(DinnerRequest dinnerRequest, String imageUrl) {
        // Validate host ID
        if (!userServiceClient.isUserHost(dinnerRequest.getHostId())) {
            throw new IllegalArgumentException("Invalid host ID: User does not exist or is not a host");
        }

        // Validate menu ID
        if (!menuServiceClient.menuExists(dinnerRequest.getMenuId())) {
            throw new IllegalArgumentException("Invalid menu ID: Menu does not exist");
        }

        Dinner dinner = mapToDinner(dinnerRequest);

        if (!dinner.isValid()) {
            throw new IllegalArgumentException("Invalid dinner: " + String.join(", ", dinner.getErrors()));
        }
        dinner.setImageUrl(imageUrl);
        Dinner savedDinner = dinnerRepository.save(dinner);
        com.buberdinner.dinnerservice.presentation.dto.DinnerResponse d = new com.buberdinner.dinnerservice.presentation.dto.DinnerResponse();
        d.setId(savedDinner.getId());
        d.setName(savedDinner.getName());
        d.setStartTime(savedDinner.getStartTime());
        d.setEndTime(savedDinner.getEndTime());
        d.setAddress(savedDinner.getAddress());
        d.setCuisineType(savedDinner.getCuisineType());
        d.setMaxGuestCount(savedDinner.getMaxGuestCount());
        d.setStatus(savedDinner.getStatus().name());
        d.setHostId(savedDinner.getHostId());
        d.setMenuId(savedDinner.getMenuId());
        d.setDescription(savedDinner.getDescription());
        d.setPrice(savedDinner.getPrice());
        DinnerCreatedEvent event = new DinnerCreatedEvent(d);
        applicationEventPublisher.publishEvent(event);

        log.info("Saved dinner: {}", savedDinner);
        return mapToDinnerResponse(savedDinner);
    }

    @Override
    public DinnerResponse updateDinner(Long id, DinnerRequest dinnerRequest) {
        Dinner existingDinner = dinnerRepository.findById(id)
                .orElseThrow(() -> new DinnerNotFoundException(id));

        updateDinnerFromRequest(existingDinner, dinnerRequest);

        if (!existingDinner.isValid()) {
            throw new IllegalArgumentException("Invalid dinner: " + String.join(", ", existingDinner.getErrors()));
        }

        Dinner updatedDinner = dinnerRepository.save(existingDinner);
        com.buberdinner.dinnerservice.presentation.dto.DinnerResponse d = new com.buberdinner.dinnerservice.presentation.dto.DinnerResponse();
        d.setId(updatedDinner.getId());
        d.setName(updatedDinner.getName());
        d.setStartTime(updatedDinner.getStartTime());
        d.setEndTime(updatedDinner.getEndTime());
        d.setAddress(updatedDinner.getAddress());
        d.setCuisineType(updatedDinner.getCuisineType());
        d.setMaxGuestCount(updatedDinner.getMaxGuestCount());
        d.setStatus(updatedDinner.getStatus().name());
        d.setHostId(updatedDinner.getHostId());
        d.setMenuId(updatedDinner.getMenuId());
        d.setDescription(updatedDinner.getDescription());
        d.setPrice(updatedDinner.getPrice());
        DinnerUpdatedEvent event = new DinnerUpdatedEvent(d);
        applicationEventPublisher.publishEvent(event);

        return mapToDinnerResponse(updatedDinner);
    }

    @Override
    public void deleteDinner(Long id) {
        Dinner dinner = dinnerRepository.findById(id)
                .orElseThrow(() -> new DinnerNotFoundException(id));



        dinnerRepository.save(dinner);
    }

    @Override
    public DinnerResponse getDinnerById(Long id) {
        Dinner dinner = dinnerRepository.findById(id)
                .orElseThrow(() -> new DinnerNotFoundException(id));

        // Update status based on current time
       // dinner.updateStatus(LocalDateTime.now());
        if (dinner.getStatus() != DinnerStatus.UPCOMING) {
            dinnerRepository.save(dinner);
        }

        return mapToDinnerResponse(dinner);
    }

    @Override
    public List<DinnerResponse> getAllDinners() {
        List<Dinner> dinners = dinnerRepository.findAll();

        // Update status of all dinners based on current time
        LocalDateTime now = LocalDateTime.now();
        List<Dinner> dinnersToUpdate = dinners.stream()
                .filter(dinner -> dinner.getStatus() == DinnerStatus.UPCOMING || 
                                  dinner.getStatus() == DinnerStatus.IN_PROGRESS)

                .filter(dinner -> dinner.getStatus() != DinnerStatus.UPCOMING)
                .collect(Collectors.toList());



        return dinners.stream()
                .map(this::mapToDinnerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DinnerResponse> getDinnersByHostId(Long hostId) {
        List<Dinner> dinners = dinnerRepository.findByHostId(hostId);

        // Update status of all dinners based on current time
        LocalDateTime now = LocalDateTime.now();
        List<Dinner> dinnersToUpdate = dinners.stream()
                .filter(dinner -> dinner.getStatus() == DinnerStatus.UPCOMING || 
                                  dinner.getStatus() == DinnerStatus.IN_PROGRESS)
                .filter(dinner -> dinner.getStatus() != DinnerStatus.UPCOMING)
                .collect(Collectors.toList());



        return dinners.stream()
                .map(this::mapToDinnerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DinnerResponse> getDinnersByMenuId(Long menuId) {
        List<Dinner> dinners = dinnerRepository.findByMenuId(menuId);

        // Update status of all dinners based on current time
        LocalDateTime now = LocalDateTime.now();
        List<Dinner> dinnersToUpdate = dinners.stream()
                .filter(dinner -> dinner.getStatus() == DinnerStatus.UPCOMING || 
                                  dinner.getStatus() == DinnerStatus.IN_PROGRESS)
                .filter(dinner -> dinner.getStatus() != DinnerStatus.UPCOMING)
                .collect(Collectors.toList());



        return dinners.stream()
                .map(this::mapToDinnerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean RescheduleDinner(long dinnerId, LocalDateTime newStart, LocalDateTime newEnd, List<String> errors) {
        Optional<Dinner> optionalDinner = dinnerRepository.findById(dinnerId);
        if (optionalDinner.isEmpty()) {
            errors.add("Dinner not found");
            return false;
        }

        Dinner dinner = optionalDinner.get();
        boolean result = dinner.reschedule(newStart, newEnd);
        errors.addAll(dinner.getErrors());

        if (result) {
            dinnerRepository.save(dinner);
        }

        return result;
    }

    @Transactional
    public void startDinner(Long dinnerId) {
        Dinner dinner = dinnerRepository.findById(dinnerId)
                .orElseThrow(() -> new NotFoundException("Dinner not found"));

        if (dinner.getStatus() != DinnerStatus.UPCOMING) {
            throw new IllegalStateException("Dinner must be UPCOMING to start");
        }

        if (LocalDateTime.now().isBefore(dinner.getStartTime())) {
            throw new IllegalStateException("Dinner cannot start before scheduled time");
        }

        dinner.start();
        Dinner dinner1 = dinnerRepository.save(dinner);
        try {
            String url = reservationServiceUrl + "/api/v1/reservations/dinner" + "/" + dinner.getId() ;

            GuestIdListResponse response = restTemplate.getForObject(url, GuestIdListResponse.class);

            DinnerStartedEvent event = new DinnerStartedEvent(mapToDinnerResponse(dinner1),response.getGuestIds());

            applicationEventPublisher.publishEvent(event);

            System.out.println("✅ Event envoyé : " + event);
        } catch (Exception e) {
            System.err.println("❌ Échec récupération guests ou envoi Kafka : " + e.getMessage());
            // Tu peux ici logguer, rethrow, ou gérer avec un fallback si nécessaire
        }
    }

    @Transactional
    public void completeDinner(Long dinnerId) {
        Dinner dinner = dinnerRepository.findById(dinnerId)
                .orElseThrow(() -> new NotFoundException("Dinner not found"));

        if (dinner.getStatus() != DinnerStatus.IN_PROGRESS) {
            throw new IllegalStateException("Dinner cannot be completed - invalid status");
        }

        dinner.complete();
        dinnerRepository.save(dinner);
    }

    @Transactional
    public void startAllDinnersInMenu(Long menuId) {
        List<Dinner> dinners = dinnerRepository.findByMenuIdAndStatus(menuId, DinnerStatus.UPCOMING);

        LocalDateTime now = LocalDateTime.now();
        for (Dinner dinner : dinners) {
            if (now.isAfter(dinner.getStartTime())) {
                dinner.start();
            }
        }
        dinnerRepository.saveAll(dinners);
    }

//    @Scheduled(fixedRate = 60000)
//    @Transactional// Toutes les 60 secondes
//    public void checkAndUpdateDinners() {
//        List<Dinner> dinners = dinnerRepository.findAll();
//
//        for (Dinner dinner : dinners) {
//            boolean changed = false;
//
//            if (dinner.getStatus() == DinnerStatus.UPCOMING && LocalDateTime.now().isAfter(dinner.getStartTime())) {
//                changed = dinner.start();
//                System.out.println("Dinner started: " + dinner.getName() + " at " + dinner.getStartTime() + " with " + dinner.getMaxGuestCount() );
//            }
//
//            if (dinner.getStatus() == DinnerStatus.IN_PROGRESS && LocalDateTime.now().isAfter(dinner.getEndTime())) {
//                changed = dinner.complete();
//            }
//
//            if (changed) {
//                dinnerRepository.save(dinner);
//            }
//        }
//    }

    private Dinner mapToDinner(DinnerRequest dinnerRequest) {
        return new Dinner(
                null,
                dinnerRequest.getHostId(),
                dinnerRequest.getMenuId(),
                dinnerRequest.getName(),
                dinnerRequest.getDescription(),
                dinnerRequest.getPrice(),
                dinnerRequest.getStartTime(),
                dinnerRequest.getEndTime(),
                dinnerRequest.getAddress(),
                dinnerRequest.getCuisineType(),
                dinnerRequest.getMaxGuestCount(),
                null

        );
    }

    private void updateDinnerFromRequest(Dinner dinner, DinnerRequest dinnerRequest) {
        dinner.setHostId(dinnerRequest.getHostId());
        dinner.setMenuId(dinnerRequest.getMenuId());
        dinner.setName(dinnerRequest.getName());
        dinner.setDescription(dinnerRequest.getDescription());
        dinner.setPrice(dinnerRequest.getPrice());
        dinner.setStartTime(dinnerRequest.getStartTime());
        dinner.setEndTime(dinnerRequest.getEndTime());
        dinner.setAddress(dinnerRequest.getAddress());
        dinner.setCuisineType(dinnerRequest.getCuisineType());
        dinner.setMaxGuestCount(dinnerRequest.getMaxGuestCount());
    }

    private DinnerResponse mapToDinnerResponse(Dinner dinner) {
        return new DinnerResponse(
                dinner.getId(),
                dinner.getHostId(),
                dinner.getMenuId(),
                dinner.getName(),
                dinner.getDescription(),
                dinner.getPrice(),
                dinner.getStartTime(),
                dinner.getEndTime(),
                dinner.getImageUrl(),
                dinner.getAddress(),
                dinner.getCuisineType(),
                dinner.getMaxGuestCount(),
                dinner.getStatus().name(),
                reviewApplicationService.meanReviewsByDinnerId(dinner.getId())

        );
    }
}
