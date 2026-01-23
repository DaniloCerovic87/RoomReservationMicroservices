package com.roomreservation.reservationservice.dto;

import com.roomreservation.reservationservice.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long id,
        String reservationStatus,
        String reservationName,
        String reservationType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long employeeId,
        String employeeName,
        List<Long> roomIds
) {

    public static ReservationResponse toResponse(
            Reservation reservation,
            List<Long> roomIds,
            String employeeName
    ) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationStatus().name(),
                reservation.getReservationName(),
                reservation.getReservationType().name(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getEmployeeId(),
                employeeName,
                roomIds
        );
    }
}
