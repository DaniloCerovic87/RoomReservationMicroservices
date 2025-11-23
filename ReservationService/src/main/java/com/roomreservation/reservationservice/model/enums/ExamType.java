package com.roomreservation.reservationservice.model.enums;

import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExamType {
    WRITTEN("Written"),
    ORAL("Oral"),
    SEMINAR("Seminar");

    private final String value;

    public static ExamType fromValue(String value) {
        for (ExamType type : ExamType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ValidationException("Unknown exam type: {0}", value);
    }
}