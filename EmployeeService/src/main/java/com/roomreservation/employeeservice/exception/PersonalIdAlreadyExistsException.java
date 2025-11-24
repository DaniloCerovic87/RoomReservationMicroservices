package com.roomreservation.employeeservice.exception;

public class PersonalIdAlreadyExistsException extends RuntimeException {
    public PersonalIdAlreadyExistsException(String personalId) {
        super("Personal ID: " + personalId + " already exists");
    }
}
