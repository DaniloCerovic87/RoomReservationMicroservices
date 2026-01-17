package com.roomreservation.reservationservice.validation;

import com.roomreservation.reservationservice.dto.ReservationRequest;

public interface ReservationValidationStrategy {

    void validate(ReservationRequest request);

}
