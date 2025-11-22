package com.roomreservation.roomservice.util;

import com.roomreservation.roomservice.exception.ValidationException;
import com.roomreservation.roomservice.model.enums.RoomType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoomValidator {

    public static void validateRoom(String roomType, Integer numberOfComputers) {
        if (roomType.equals(RoomType.COMPUTER_ROOM.getValue()) &&
                (numberOfComputers == null || numberOfComputers <= 0)) {
            throw new ValidationException("Computer room must have a valid number of computers");
        }

        if ((roomType.equals(RoomType.CLASSROOM.getValue()) ||
                roomType.equalsIgnoreCase(RoomType.AMPHITHEATER.getValue())) && numberOfComputers != null) {
            throw new ValidationException("Only computer rooms can have a number of computers defined");
        }
    }

}
