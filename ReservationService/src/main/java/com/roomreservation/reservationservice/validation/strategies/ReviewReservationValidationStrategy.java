package com.roomreservation.reservationservice.validation.strategies;

import com.roomreservation.reservationservice.dto.ReviewReservationRequest;
import com.roomreservation.reservationservice.validation.ReservationValidationChain;
import com.roomreservation.reservationservice.validation.ReservationValidationStrategy;
import com.roomreservation.reservationservice.validation.rules.DistinctApproveRoomIdsRule;
import com.roomreservation.reservationservice.validation.rules.DistinctDeclineRoomIdsRule;
import com.roomreservation.reservationservice.validation.rules.NoOverlapApproveDeclineRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewReservationValidationStrategy
        implements ReservationValidationStrategy<ReviewReservationRequest> {

    private final ReservationValidationChain<ReviewReservationRequest> chain =
        new ReservationValidationChain<>(List.of(
            new DistinctApproveRoomIdsRule(),
            new DistinctDeclineRoomIdsRule(),
            new NoOverlapApproveDeclineRule()
        ));

    @Override
    public void validate(ReviewReservationRequest request) {
        chain.validate(request);
    }
}
