package com.roomreservation.employeeservice.exception.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthServiceException extends RuntimeException {

    private final HttpStatus backendStatus;
    private final String backendBody;

    public AuthServiceException(String message, HttpStatus backendStatus, String backendBody, Throwable cause) {
        super(message, cause);
        this.backendStatus = backendStatus;
        this.backendBody = backendBody;
    }
}
