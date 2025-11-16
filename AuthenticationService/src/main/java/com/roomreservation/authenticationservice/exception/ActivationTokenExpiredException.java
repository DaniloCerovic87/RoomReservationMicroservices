package com.roomreservation.authenticationservice.exception;

import java.time.LocalDateTime;

public class ActivationTokenExpiredException extends RuntimeException {

    public ActivationTokenExpiredException(LocalDateTime expiresAt) {
        super("Activation token expired at: " + expiresAt);
    }
}
