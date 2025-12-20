package com.roomreservation.employeeservice.enums;

import com.roomreservation.employeeservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcademicRank {
    ASSISTANT("Assistant"),
    DOCENT("Docent"),
    ASSOCIATE_PROFESSOR("Associate Professor"),
    FULL_PROFESSOR("Full Professor");

    private final String value;

    public static AcademicRank fromValue(String value) {
        for (AcademicRank type : AcademicRank.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ValidationException(String.format("Unknown academic rank: %s", value));
    }

}