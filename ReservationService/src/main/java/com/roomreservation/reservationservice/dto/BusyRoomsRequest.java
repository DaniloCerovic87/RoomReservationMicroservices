package com.roomreservation.reservationservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BusyRoomsRequest(
        @NotNull(message = "Reservation start time is required")
        @FutureOrPresent(message = "Reservation start time must be in the present or future")
        LocalDateTime startTime,

        @NotNull(message = "Reservation end time is required")
        @FutureOrPresent(message = "Reservation end time must be in the present or future")
        LocalDateTime endTime
) implements HasTimeRange {
}
