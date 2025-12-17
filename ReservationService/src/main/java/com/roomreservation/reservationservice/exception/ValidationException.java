package com.roomreservation.reservationservice.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, String value) {
        super(String.format(message, value));
    }

}
