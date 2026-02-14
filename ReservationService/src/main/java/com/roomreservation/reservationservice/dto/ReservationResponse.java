package com.roomreservation.reservationservice.dto;

import com.roomreservation.reservationservice.model.Reservation;
import com.roomreservation.reservationservice.model.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long id,
        String reservationName,
        String reservationStatus,
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
            String employeeName,
            ReservationStatus status
    ) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationName(),
                status.getValue(),
                reservation.getReservationType().getValue(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getEmployeeId(),
                employeeName,
                roomIds
        );
    }
}
