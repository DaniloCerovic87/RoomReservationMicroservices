package com.roomreservation.reservationservice.validation.rules;

import com.roomreservation.reservationservice.dto.ReviewReservationRequest;
import com.roomreservation.reservationservice.validation.ReservationRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistinctApproveRoomIdsRule implements ReservationRule<ReviewReservationRequest> {

    @Override
    public void validate(ReviewReservationRequest request, List<String> errors) {
        var approve = request.approveRoomIds();
        if (approve == null || approve.isEmpty()) {
            return; // ok: approve može da bude prazno
        }

        Set<Long> unique = new HashSet<>(approve);
        if (unique.size() != approve.size()) {
            errors.add("DUPLICATE_ROOM_ID_IN_APPROVE_LIST");
        }
    }
}
