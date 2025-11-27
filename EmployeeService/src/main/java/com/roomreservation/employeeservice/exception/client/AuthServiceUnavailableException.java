package com.roomreservation.employeeservice.exception.client;

public class AuthServiceUnavailableException extends RuntimeException {

    public AuthServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
