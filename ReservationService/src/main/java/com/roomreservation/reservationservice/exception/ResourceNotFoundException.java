package com.roomreservation.reservationservice.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    public ResourceNotFoundException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}