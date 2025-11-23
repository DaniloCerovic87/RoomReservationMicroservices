package com.roomreservation.reservationservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(

        @NotNull(message = "Employee ID is required")
        Long employeeId,

        @NotNull(message = "Room ID is required")
        Long roomId,

        @NotNull(message = "Reservation start time is required")
        @FutureOrPresent(message = "Reservation start time must be in the present or future")
        LocalDateTime startTime,

        @NotNull(message = "Reservation end time is required")
        @FutureOrPresent(message = "Reservation end time must be in the present or future")
        LocalDateTime endTime,

        @NotNull(message = "Reservation purpose is required")
        String reservationPurpose,

        String subject,
        Integer semester,
        String classType,
        String examType,

        String meetingName,
        String meetingDescription,

        String eventName,
        String eventDescription

) {
}
