package com.roomreservation.notificationservice.messaging.event;

import java.time.LocalDateTime;

public record UserInvitedEvent(
        Long userId,
        Long employeeId,
        String email,
        String firstName,
        String activationToken,
        LocalDateTime expiresAt
) {
}
