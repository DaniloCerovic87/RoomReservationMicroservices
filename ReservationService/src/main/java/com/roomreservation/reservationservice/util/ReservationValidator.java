package com.roomreservation.reservationservice.util;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.exception.ValidationException;
import com.roomreservation.reservationservice.model.enums.ReservationStatus;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ReservationValidator {

    private static final LocalTime OPEN = LocalTime.of(8, 0);
    private static final LocalTime CLOSE = LocalTime.of(20, 0);

    public static void validateForCreate(ReservationRequest request) {
        validateTimeRange(request);
        validateWorkingHours(request);
        validateDistinctRoomIds(request);
    }

    private static void validateTimeRange(ReservationRequest request) {
        LocalDateTime start = request.startTime();
        LocalDateTime end = request.endTime();

        if (!end.isAfter(start)) {
            throw new ValidationException("Reservation end time must be after start time");
        }

        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 15) {
            throw new ValidationException("Reservation must be at least 5 minutes long");
        }
    }

    private static void validateWorkingHours(ReservationRequest request) {
        LocalDateTime start = request.startTime();
        LocalDateTime end = request.endTime();

        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new ValidationException("Reservation must be within a single day.");
        }

        if (start.toLocalTime().isBefore(OPEN) || end.toLocalTime().isAfter(CLOSE)) {
            throw new ValidationException("Reservations are allowed only between 08:00 and 20:00.");
        }
    }


    private static void validateDistinctRoomIds(ReservationRequest request) {
        Set<Long> unique = new HashSet<>(request.roomIds());
        if (unique.size() != request.roomIds().size()) {
            throw new ValidationException("You can't select the same room more than once.");
        }
    }

    public static void validateTransition(ReservationStatus current, ReservationStatus target) {
        if (!current.canTransitionTo(target)) {
            throw new ValidationException("Invalid status transition: " + current + " -> " + target);
        }
    }

}
