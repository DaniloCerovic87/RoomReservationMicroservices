package com.roomreservation.employeeservice.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
