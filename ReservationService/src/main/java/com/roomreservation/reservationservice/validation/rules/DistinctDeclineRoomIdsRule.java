package com.roomreservation.reservationservice.validation.rules;

import com.roomreservation.reservationservice.dto.ReviewReservationRequest;
import com.roomreservation.reservationservice.validation.ReservationRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistinctDeclineRoomIdsRule implements ReservationRule<ReviewReservationRequest> {

    @Override
    public void validate(ReviewReservationRequest request, List<String> errors) {
        var decline = request.declineRooms();
        if (decline == null || decline.isEmpty()) {
            return;
        }

        Set<Long> seen = new HashSet<>();
        for (var d : decline) {
            Long roomId = d.roomId();
            if (roomId == null) {
                errors.add("ROOM_ID_REQUIRED_IN_DECLINE_LIST");
                continue;
            }

            if (!seen.add(roomId)) {
                errors.add("DUPLICATE_ROOM_ID_IN_DECLINE_LIST");
            }
        }
    }
}
