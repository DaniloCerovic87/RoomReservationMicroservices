package com.roomreservation.reservationservice.validation;

import java.util.List;

public interface ReservationRule<T> {

    void validate(T request, List<String> errors);

}
