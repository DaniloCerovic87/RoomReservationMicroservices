package com.roomreservation.reservationservice.validation;

import com.roomreservation.reservationservice.dto.ReservationRequest;

import java.util.List;

public interface ReservationRule {

    void validate(ReservationRequest request, List<String> errors);

}
