package com.roomreservation.reservationservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(String message) {
        super(message);
        errors = null;
    }

    public ValidationException(String message, String value) {
        super(String.format(message, value));
        errors = null;
    }

    public ValidationException(List<String> errors) {
        super("Validation failed");
        this.errors = List.copyOf(errors);
    }

}
