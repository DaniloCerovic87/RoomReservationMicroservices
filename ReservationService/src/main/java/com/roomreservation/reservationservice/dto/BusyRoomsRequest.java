package com.roomreservation.reservationservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BusyRoomsRequest(
        @NotNull(message = "START_TIME_REQUIRED")
        @FutureOrPresent(message = "START_TIME_MUST_BE_PRESENT_OR_FUTURE")
        LocalDateTime startTime,

        @NotNull(message = "END_TIME_REQUIRED")
        @FutureOrPresent(message = "END_TIME_MUST_BE_PRESENT_OR_FUTURE")
        LocalDateTime endTime
) implements HasTimeRange {
}
