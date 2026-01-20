package com.roomreservation.reservationservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationRequest(

        @NotNull(message = "Employee ID is required")
        Long employeeId,

        @NotEmpty
        List<@NotNull Long> roomIds,

        @NotBlank(message = "Reservation name is required")
        String reservationName,

        @NotNull
        String reservationType,

        @NotNull(message = "Reservation start time is required")
        @FutureOrPresent(message = "Reservation start time must be in the present or future")
        LocalDateTime startTime,

        @NotNull(message = "Reservation end time is required")
        @FutureOrPresent(message = "Reservation end time must be in the present or future")
        LocalDateTime endTime

) implements HasTimeRange {
}
