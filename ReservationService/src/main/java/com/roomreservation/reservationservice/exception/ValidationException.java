package com.roomreservation.reservationservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errorCodes;

    public ValidationException(String errorCode) {
        super("Validation failed");
        this.errorCodes = List.of(errorCode);
    }

    public ValidationException(List<String> errorCodes) {
        super("Validation failed");
        this.errorCodes = List.copyOf(errorCodes);
    }

}
