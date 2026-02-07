package com.roomreservation.reservationservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReviewReservationRequest(
        List<Long> approveRoomIds,
        List<@Valid DeclineRoomRequest> declineRooms
) {
    public ReviewReservationRequest {
        approveRoomIds = approveRoomIds == null ? List.of() : List.copyOf(approveRoomIds);
        declineRooms = declineRooms == null ? List.of() : List.copyOf(declineRooms);
    }

    public record DeclineRoomRequest(
            @NotNull Long roomId,
            @NotNull @Size(min = 3, max = 500) String comment
    ) {}
}