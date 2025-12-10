package com.roomreservation.roomservice.dto;

import com.roomreservation.roomservice.model.Amphitheater;
import com.roomreservation.roomservice.model.Classroom;
import com.roomreservation.roomservice.model.ComputerRoom;
import com.roomreservation.roomservice.model.Room;
import com.roomreservation.roomservice.model.enums.RoomType;

public record RoomResponse(
        Long id,
        String name,
        String roomType,
        Integer capacity,
        Integer numberOfComputers,
        Integer numberOfProjectors,
        Boolean hasSmartBoard

) {

    public static RoomResponse fromEntity(Room room) {
        Integer numberOfComputers = null;
        Integer numberOfProjectors = null;
        Boolean hasSmartBoard = null;

        if (room instanceof ComputerRoom computerRoom) {
            numberOfComputers = computerRoom.getNumberOfComputers();
        }

        if (room instanceof Amphitheater amphitheater) {
            numberOfProjectors = amphitheater.getNumberOfProjectors();
        }

        if (room instanceof Classroom classroom) {
            hasSmartBoard = classroom.getHasSmartBoard();
        }

        return new RoomResponse(
                room.getId(),
                room.getName(),
                RoomType.valueOf(room.getRoomType()).getValue(),
                room.getCapacity(),
                numberOfComputers,
                numberOfProjectors,
                hasSmartBoard
        );
    }
}

