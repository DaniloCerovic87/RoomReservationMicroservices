package com.roomreservation.reservationservice.validation.rules;

import com.roomreservation.reservationservice.dto.ReviewReservationRequest;
import com.roomreservation.reservationservice.validation.ReservationRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoOverlapApproveDeclineRule implements ReservationRule<ReviewReservationRequest> {

    @Override
    public void validate(ReviewReservationRequest request, List<String> errors) {
        var approve = request.approveRoomIds();
        var decline = request.declineRooms();

        if (approve == null || approve.isEmpty() || decline == null || decline.isEmpty()) {
            return;
        }

        Set<Long> approveSet = new HashSet<>(approve);

        for (var d : decline) {
            Long roomId = d.roomId();
            if (roomId == null) {
                continue;
            }
            if (approveSet.contains(roomId)) {
                errors.add("ROOM_ID_IN_BOTH_APPROVE_AND_DECLINE");
                return;
            }
        }
    }
}
