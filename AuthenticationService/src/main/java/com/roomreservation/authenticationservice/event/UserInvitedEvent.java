package com.roomreservation.authenticationservice.event;

import java.time.LocalDateTime;

public record UserInvitedEvent(
        Long userId,
        String email,
        String firstName,
        String activationToken,
        LocalDateTime expiresAt
) {}
