package com.roomreservation.reservationservice.controller;

import com.roomreservation.reservationservice.dto.BusyRoomsRequest;
import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;
import com.roomreservation.reservationservice.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse createdReservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        reservationService.approveReservation(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<Void> decline(@PathVariable Long id) {
        reservationService.declineReservation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/busy-room-ids")
    public ResponseEntity<List<Long>> busyRoomIds(
            @Valid @ModelAttribute BusyRoomsRequest request
    ) {
        return ResponseEntity.ok(reservationService.findBusyRoomIds(request));
    }


}