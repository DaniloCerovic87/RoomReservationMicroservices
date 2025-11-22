package com.roomreservation.roomservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RoomRequest(

        @NotBlank(message = "Room name must not be blank")
        String name,

        @NotBlank(message = "Room type must be defined")
        String roomType,

        @NotNull(message = "Room capacity must be defined")
        @Positive(message = "Room capacity must be a positive number")
        Integer capacity,

        Integer numberOfComputers
) {
}