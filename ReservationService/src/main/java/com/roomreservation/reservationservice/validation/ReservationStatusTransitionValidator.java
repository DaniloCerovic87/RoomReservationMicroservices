package com.roomreservation.reservationservice.validation;

import com.roomreservation.reservationservice.exception.ValidationException;
import com.roomreservation.reservationservice.model.enums.ReservationStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReservationStatusTransitionValidator {

    public static void validateTransition(ReservationStatus current, ReservationStatus target) {
        if (!current.canTransitionTo(target)) {
            throw new ValidationException("Invalid status transition: " + current + " -> " + target);
        }
    }

}
