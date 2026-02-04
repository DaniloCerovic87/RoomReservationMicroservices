package com.roomreservation.authenticationservice.messaging.event;

import java.time.LocalDateTime;

public record UserInvitedEvent(
        Long userId,
        String email,
        String firstName,
        String activationToken,
        LocalDateTime expiresAt
) {
}
