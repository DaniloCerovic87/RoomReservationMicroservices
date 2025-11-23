package com.roomreservation.reservationservice.model.enums;

import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClassType {
    LECTURE("Lecture"),
    EXERCISE("Exercise");

    private final String value;

    public static ClassType fromValue(String value) {
        for (ClassType purpose : ClassType.values()) {
            if (purpose.value.equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new ValidationException("Unknown class type: {0}", value);
    }
}