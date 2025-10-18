package com.buberdinner.dinnerservice.presentation.controller;

import com.buberdinner.dinnerservice.infrastructure.client.ImageService;
import com.buberdinner.dinnerservice.presentation.dto.DinnerUpdateRequest;
import com.buberdinner.dinnerservice.presentation.dto.DinnerRequest;
import com.buberdinner.dinnerservice.presentation.dto.DinnerResponse;
import com.buberdinner.dinnerservice.application.service.DinnerApplicationService;
import com.buberdinner.dinnerservice.presentation.dto.RescheduleDinnerRequest;
import com.buberdinner.dinnerservice.presentation.mapper.DinnerDtoMapper;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dinners")
@RequiredArgsConstructor
public class DinnerController {

    private final DinnerApplicationService dinnerApplicationService;
    private final DinnerDtoMapper dinnerDtoMapper;
    private final ImageService imageService;

//    @PostMapping
//    public ResponseEntity<DinnerResponse> createDinner(@RequestBody DinnerRequest dinnerRequest) {
//        com.buberdinner.dinnerservice.application.dto.DinnerRequest applicationRequest = dinnerDtoMapper.toApplicationRequest(dinnerRequest);
//        com.buberdinner.dinnerservice.application.dto.DinnerResponse applicationResponse = dinnerApplicationService.createDinner(applicationRequest);
//        DinnerResponse presentationResponse = dinnerDtoMapper.toPresentationResponse(applicationResponse);
//        return new ResponseEntity<>(presentationResponse, HttpStatus.CREATED);
//    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DinnerResponse> createDinner(
            @RequestPart("dinner") DinnerRequest dinnerRequest,
            @RequestPart("image") MultipartFile imageFile) {

        // 1. Upload image
        String imageUrl = imageService.storeImage(imageFile);


        com.buberdinner.dinnerservice.application.dto.DinnerRequest appRequest = dinnerDtoMapper.toApplicationRequest(dinnerRequest);

        // 🧠 3. Injecte imageUrl ici, manuellement si nécessaire
        // Si DinnerRequest est immutable : crée une copie avec imageUrl
        // Sinon, ajoute un setter temporaire (ou utilise un builder)

//        DinnerRequest requestWithImage = new DinnerRequest();
//        requestWithImage.setHostId(appRequest.getHostId());
//        requestWithImage.setMenuId(appRequest.getMenuId());
//        requestWithImage.setStartTime(appRequest.getStartTime());
//        requestWithImage.setEndTime(appRequest.getEndTime());
//        requestWithImage.setAddress(appRequest.getAddress());
//        requestWithImage.setDescription(appRequest.getDescription());
//        requestWithImage.setCuisineType(appRequest.getCuisineType());
//        requestWithImage.setMaxGuestCount(appRequest.getMaxGuestCount());

        // Astuce : passer imageUrl à l'applicationService séparément ?
        com.buberdinner.dinnerservice.application.dto.DinnerResponse response = dinnerApplicationService.createDinner(appRequest, imageUrl);

        return new ResponseEntity<>(
                dinnerDtoMapper.toPresentationResponse(response),
                HttpStatus.CREATED
        );
    }


    @PostMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleDinner(@PathVariable long id, @RequestBody RescheduleDinnerRequest request) {
        List<String> errors = new ArrayList<>();
        boolean success = dinnerApplicationService.RescheduleDinner(id, request.newStartTime, request.newEndTime, errors);

        if (success) {
            return ResponseEntity.ok("Dinner successfully rescheduled.");
        } else {
            return ResponseEntity.badRequest().body(errors);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DinnerResponse> updateDinner(@PathVariable Long id, @RequestBody DinnerRequest dinnerRequest) {
        com.buberdinner.dinnerservice.application.dto.DinnerRequest applicationRequest = dinnerDtoMapper.toApplicationRequest(dinnerRequest);
        com.buberdinner.dinnerservice.application.dto.DinnerResponse applicationResponse = dinnerApplicationService.updateDinner(id, applicationRequest);
        DinnerResponse presentationResponse = dinnerDtoMapper.toPresentationResponse(applicationResponse);
        return ResponseEntity.ok(presentationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDinner(@PathVariable Long id) {
        dinnerApplicationService.deleteDinner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DinnerResponse> getDinnerById(@PathVariable Long id) {
        com.buberdinner.dinnerservice.application.dto.DinnerResponse applicationResponse = dinnerApplicationService.getDinnerById(id);
        DinnerResponse presentationResponse = dinnerDtoMapper.toPresentationResponse(applicationResponse);
        return ResponseEntity.ok(presentationResponse);
    }

    @GetMapping
    public ResponseEntity<List<DinnerResponse>> getAllDinners() {
        List<com.buberdinner.dinnerservice.application.dto.DinnerResponse> applicationResponses = dinnerApplicationService.getAllDinners();
        List<DinnerResponse> presentationResponses = dinnerDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<DinnerResponse>> getDinnersByHostId(@PathVariable Long hostId) {
        List<com.buberdinner.dinnerservice.application.dto.DinnerResponse> applicationResponses = dinnerApplicationService.getDinnersByHostId(hostId);
        List<DinnerResponse> presentationResponses = dinnerDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<List<DinnerResponse>> getDinnersByMenuId(@PathVariable Long menuId) {
        List<com.buberdinner.dinnerservice.application.dto.DinnerResponse> applicationResponses = dinnerApplicationService.getDinnersByMenuId(menuId);
        List<DinnerResponse> presentationResponses = dinnerDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }

    @PostMapping("/{dinnerId}/start")
    public ResponseEntity<?> startDinner(@PathVariable Long dinnerId) {
        try {
            dinnerApplicationService.startDinner(dinnerId);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Dinner started successfully",
                    "status", HttpStatus.OK.value()
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND.value())
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage(), "status", HttpStatus.BAD_REQUEST.value())
            );
        }
    }

    @PostMapping("/{dinnerId}/complete")
    public ResponseEntity<?> completeDinner(@PathVariable Long dinnerId) {
        try {
            dinnerApplicationService.completeDinner(dinnerId);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Dinner completed successfully",
                    "status", HttpStatus.OK.value()
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND.value())
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage(), "status", HttpStatus.BAD_REQUEST.value())
            );
        }
    }

    @PostMapping("/menu/{menuId}/start-all")
    public ResponseEntity<?> startAllDinnersInMenu(@PathVariable Long menuId) {
        try {
            dinnerApplicationService.startAllDinnersInMenu(menuId);
            return ResponseEntity.ok().body(Map.of(
                    "message", "All dinners in menu started successfully",
                    "status", HttpStatus.OK.value()
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", e.getMessage(), "status", HttpStatus.NOT_FOUND.value())
            );
        }
    }
}
