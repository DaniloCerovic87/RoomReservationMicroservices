package com.roomreservation.reservationservice.model.enums;

import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationType {
    BASIC("Basic"),
    MASTER("Master"),
    SPECIALIST("Specialist"),
    DOCTORAL("Doctoral");

    private final String value;

    public static ReservationType fromValue(String value) {
        for (ReservationType status : ReservationType.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new ValidationException("INVALID_RESERVATION_TYPE");
    }
}