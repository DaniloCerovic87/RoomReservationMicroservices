package com.roomreservation.reservationservice.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationCreatedEvent(

        Long reservationId,
        EmployeeSnapshot employee,
        String reservationName,
        String reservationType,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime occurredAt,
        List<RoomSnapshot> rooms

) {

    public record EmployeeSnapshot(Long employeeId, String fullName) {
    }

    public record RoomSnapshot(Long roomId, String name) {
    }

}