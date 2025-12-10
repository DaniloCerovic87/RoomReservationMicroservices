package com.roomreservation.roomservice.util;

import com.roomreservation.roomservice.dto.RoomUpdateRequest;
import com.roomreservation.roomservice.exception.ValidationException;
import com.roomreservation.roomservice.model.Amphitheater;
import com.roomreservation.roomservice.model.Classroom;
import com.roomreservation.roomservice.model.ComputerRoom;
import com.roomreservation.roomservice.model.Room;
import com.roomreservation.roomservice.model.enums.RoomType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoomValidator {

    public static void validateCreate(
            String roomType,
            Integer numberOfComputers,
            Integer numberOfProjectors,
            Boolean hasSmartBoard
    ) {

        RoomType type = RoomType.fromValue(roomType);

        switch (type) {
            case COMPUTER_ROOM -> {
                if (numberOfComputers == null || numberOfComputers <= 0) {
                    throw new ValidationException("Computer room must have a valid number of computers");
                }

                if (numberOfProjectors != null) {
                    throw new ValidationException("Computer room must NOT have projectors");
                }

                if (hasSmartBoard != null) {
                    throw new ValidationException("Computer room must NOT have a smart board");
                }
            }

            case AMPHITHEATER -> {
                if (numberOfProjectors == null || numberOfProjectors <= 0) {
                    throw new ValidationException("Amphitheater must have at least one projector");
                }

                if (numberOfComputers != null) {
                    throw new ValidationException("Amphitheater must NOT have computers");
                }
                if (hasSmartBoard != null) {
                    throw new ValidationException("Amphitheater must NOT have a smart board");
                }
            }

            case CLASSROOM -> {
                if (hasSmartBoard == null) {
                    throw new ValidationException("Classroom must define whether it has a smart board");
                }

                if (numberOfComputers != null) {
                    throw new ValidationException("Classroom must NOT have computers");
                }

                if (numberOfProjectors != null) {
                    throw new ValidationException("Classroom must NOT have projectors");
                }
            }
        }
    }

    public static void validateUpdate(Room room, RoomUpdateRequest request) {

        if (room instanceof ComputerRoom) {

            if (request.numberOfProjectors() != null) {
                throw new ValidationException("Projectors cannot be defined for computer rooms.");
            }

            if (request.hasSmartBoard() != null) {
                throw new ValidationException("Smart board can only be defined for classroom type rooms.");
            }
        }

        if (room instanceof Amphitheater) {

            if (request.numberOfComputers() != null) {
                throw new ValidationException("Computers cannot be defined for amphitheaters.");
            }

            if (request.hasSmartBoard() != null) {
                throw new ValidationException("Smart board can only be defined for classroom type rooms.");
            }
        }

        if (room instanceof Classroom) {

            if (request.numberOfComputers() != null) {
                throw new ValidationException("Computers cannot be defined for classroom type rooms.");
            }

            if (request.numberOfProjectors() != null) {
                throw new ValidationException("Projectors cannot be defined for classroom type rooms.");
            }
        }
    }

}
