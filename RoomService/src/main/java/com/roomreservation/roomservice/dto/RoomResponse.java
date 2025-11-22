package com.roomreservation.roomservice.dto;

import com.roomreservation.roomservice.model.Room;

public record RoomResponse(

        Long id,

        String name,

        String roomType,

        Integer capacity,

        Integer numberOfComputers

) {
    public static RoomResponse fromEntity(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getRoomType().getValue(),
                room.getCapacity(),
                room.getNumberOfComputers()
        );
    }
}
