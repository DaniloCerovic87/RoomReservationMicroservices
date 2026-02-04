package com.roomreservation.reservationservice.validation.rules;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.validation.ReservationRule;

import java.util.HashSet;
import java.util.List;

public class DistinctRoomIdsRule implements ReservationRule<ReservationRequest> {

    @Override
    public void validate(ReservationRequest request, List<String> errors) {
        var unique = new HashSet<>(request.roomIds());
        if (unique.size() != request.roomIds().size()) {
            errors.add("DUPLICATE_ROOMS_SELECTED");
        }
    }
}
