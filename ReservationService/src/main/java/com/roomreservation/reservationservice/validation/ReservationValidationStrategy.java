package com.roomreservation.reservationservice.validation;

public interface ReservationValidationStrategy<T> {

    void validate(T request);

}
