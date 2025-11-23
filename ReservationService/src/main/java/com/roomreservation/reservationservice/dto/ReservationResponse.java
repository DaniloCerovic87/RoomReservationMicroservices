package com.roomreservation.reservationservice.dto;

import com.roomreservation.reservationservice.model.Reservation;

public record ReservationResponse(
        Long id,
        String reservationStatus
) {

    public static ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationStatus().name()
        );
    }

}