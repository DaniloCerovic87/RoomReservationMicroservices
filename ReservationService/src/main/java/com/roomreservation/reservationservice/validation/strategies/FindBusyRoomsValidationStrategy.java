package com.roomreservation.reservationservice.validation.strategies;

import com.roomreservation.reservationservice.dto.BusyRoomsRequest;
import com.roomreservation.reservationservice.validation.ReservationValidationChain;
import com.roomreservation.reservationservice.validation.ReservationValidationStrategy;
import com.roomreservation.reservationservice.validation.rules.TimeRangeRule;
import com.roomreservation.reservationservice.validation.rules.WorkingHoursRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindBusyRoomsValidationStrategy implements ReservationValidationStrategy<BusyRoomsRequest> {

    private final ReservationValidationChain<BusyRoomsRequest> chain = new ReservationValidationChain<>(
            List.of(
                    new TimeRangeRule(),
                    new WorkingHoursRule()
            )
    );

    @Override
    public void validate(BusyRoomsRequest request) {
        chain.validate(request);
    }

}
