package com.roomreservation.reservationservice.messaging.event;

import java.time.LocalDateTime;

public record ReservationStatusChangedEvent(
        Long reservationId,
        String newStatus,
        LocalDateTime occurredAt
) {}
