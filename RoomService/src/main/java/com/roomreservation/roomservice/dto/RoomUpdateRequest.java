package com.roomreservation.roomservice.dto;

public record RoomUpdateRequest(
        String name,
        Integer capacity,

        Integer numberOfComputers,
        Integer numberOfProjectors,
        Boolean hasSmartBoard
) {}
