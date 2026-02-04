package com.roomreservation.reservationservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationRequest(

        @NotNull(message = "EMPLOYEE_ID_REQUIRED")
        Long employeeId,

        @NotEmpty(message = "ROOM_IDS_REQUIRED")
        List<@NotNull(message = "ROOM_ID_REQUIRED") Long> roomIds,

        @NotBlank(message = "RESERVATION_NAME_REQUIRED")
        String reservationName,

        @NotNull(message = "RESERVATION_TYPE_REQUIRED")
        String reservationType,

        @NotNull(message = "START_TIME_REQUIRED")
        @FutureOrPresent(message = "START_TIME_MUST_BE_PRESENT_OR_FUTURE")
        LocalDateTime startTime,

        @NotNull(message = "END_TIME_REQUIRED")
        @FutureOrPresent(message = "END_TIME_MUST_BE_PRESENT_OR_FUTURE")
        LocalDateTime endTime

) implements HasTimeRange {
}
