package com.roomreservation.roomservice.model.enums;

import com.roomreservation.roomservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    CLASSROOM("Classroom"),
    COMPUTER_ROOM("Computer Room"),
    AMPHITHEATER("Amphitheater");

    private final String value;

    public static RoomType fromValue(String value) {
        for (RoomType type : RoomType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ValidationException(String.format("Unknown room type: %s", value));
    }

}