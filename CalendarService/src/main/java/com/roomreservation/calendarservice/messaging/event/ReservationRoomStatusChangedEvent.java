package com.roomreservation.calendarservice.messaging.event;

import java.time.LocalDateTime;

public record ReservationRoomStatusChangedEvent(
        Long reservationId,
        Long roomId,
        String newStatus,
        LocalDateTime occurredAt
) {
}
