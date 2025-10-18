package com.buberdinner.reservationservice.presentation.controller;



import com.buberdinner.reservationservice.application.dto.CreateReservationCommand;
import com.buberdinner.reservationservice.application.dto.ReservationCreatedResponse;
import com.buberdinner.reservationservice.application.dto.ReservationResponse;
import com.buberdinner.reservationservice.application.service.ReservationService;
import com.buberdinner.reservationservice.domain.valueObject.GuestId;
import com.buberdinner.reservationservice.domain.valueObject.ReservationId;
import com.buberdinner.reservationservice.presentation.dto.ReservationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")

public class ReservationController {

    private final ReservationService reservationService;

    private final com.buberdinner.reservationservice.presentation.mapper.ReservationMapper presentationMapper;

    public ReservationController(ReservationService reservationService, com.buberdinner.reservationservice.presentation.mapper.ReservationMapper presentationMapper) {
        this.reservationService = reservationService;
        this.presentationMapper = presentationMapper;
    }

    @PostMapping
    public ResponseEntity<com.buberdinner.reservationservice.presentation.dto.ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        CreateReservationCommand command = presentationMapper.toCommand(request);
        ReservationCreatedResponse created = reservationService.createReservation(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new com.buberdinner.reservationservice.presentation.dto.ReservationResponse(
                        created.reservationId(),
                        request.dinnerId(),
                        request.guestId(),
                        request.reservationDate()));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(
            @PathVariable UUID reservationId) {
        ReservationResponse response = reservationService.getReservation(new ReservationId(reservationId));
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable UUID reservationId) {
        reservationService.cancelReservation(new ReservationId(reservationId));
    }

    @GetMapping("/guest/{id}")
    public ResponseEntity<Optional<ReservationResponse>> getReservationsByGuestId(@PathVariable Long id){
        Optional<ReservationResponse> responses =reservationService.getReservationsByGuestId(GuestId.generate(id));
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/dinner/{id}")
    public ResponseEntity<Optional<ReservationResponse>> getReservationsByDinnerId(@PathVariable Long id){
        Optional<ReservationResponse> responses =reservationService.getReservationsByDinnerId(id);
        return ResponseEntity.ok(responses);
    }


}