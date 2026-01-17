package com.roomreservation.reservationservice.validation.strategies;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.validation.ReservationValidationChain;
import com.roomreservation.reservationservice.validation.ReservationValidationStrategy;
import com.roomreservation.reservationservice.validation.rules.DistinctRoomIdsRule;
import com.roomreservation.reservationservice.validation.rules.TimeRangeRule;
import com.roomreservation.reservationservice.validation.rules.WorkingHoursRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateReservationValidationStrategy implements ReservationValidationStrategy {

    private final ReservationValidationChain chain = new ReservationValidationChain(
            List.of(
                    new TimeRangeRule(),
                    new WorkingHoursRule(),
                    new DistinctRoomIdsRule()
            )
    );

    @Override
    public void validate(ReservationRequest request) {
        chain.validate(request);
    }

}
