package com.roomreservation.reservationservice.util;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class ReservationValidator {

    public static void validateReservationDuration(ReservationRequest request) {
        long durationInMinutes = Duration.between(request.startTime(), request.endTime()).toMinutes();
        if (durationInMinutes < 5) {
            throw new ValidationException("Reservation must be at least 5 minutes long");
        }
    }

}
