package com.roomreservation.reservationservice.util;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ReservationValidator {

    public static void validateForCreate(ReservationRequest request) {
        validateReservationDuration(request);
        validateDistinctRoomIds(request);
    }

    private static void validateReservationDuration(ReservationRequest request) {
        long durationInMinutes = Duration.between(request.startTime(), request.endTime()).toMinutes();
        if (durationInMinutes < 5) {
            throw new ValidationException("Reservation must be at least 5 minutes long");
        }
    }

    private static void validateDistinctRoomIds(ReservationRequest request) {
        Set<Long> unique = new HashSet<>(request.roomIds());
        if (unique.size() != request.roomIds().size()) {
            throw new ValidationException("You can't select the same room more than once.");
        }
    }

}
