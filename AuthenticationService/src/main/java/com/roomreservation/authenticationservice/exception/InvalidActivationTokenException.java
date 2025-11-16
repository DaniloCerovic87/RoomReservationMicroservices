package com.roomreservation.authenticationservice.exception;

public class InvalidActivationTokenException extends RuntimeException {

    public InvalidActivationTokenException(String token) {
        super("Invalid activation token: " + token);
    }
}
