package com.roomreservation.employeeservice.messaging.event;

import java.time.LocalDateTime;

public record InviteEmailFailedFinalEvent(
        Long userId,
        Long employeeId,
        String reason,
        LocalDateTime occurredAt
) {
}