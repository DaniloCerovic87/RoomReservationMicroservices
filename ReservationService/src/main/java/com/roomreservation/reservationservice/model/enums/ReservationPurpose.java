package com.roomreservation.reservationservice.model.enums;

import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationPurpose {
    CLASS("Class"),
    EXAM("Exam"),
    MEETING("Meeting"),
    EVENT("Event");

    private final String value;

    public static ReservationPurpose fromValue(String value) {
        for (ReservationPurpose purpose : ReservationPurpose.values()) {
            if (purpose.value.equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new ValidationException("Unknown reservation purpose: {0}", value);
    }
}