package com.roomreservation.reservationservice.validation.strategies;

import com.roomreservation.reservationservice.dto.HasTimeRange;
import com.roomreservation.reservationservice.validation.ReservationValidationChain;
import com.roomreservation.reservationservice.validation.ReservationValidationStrategy;
import com.roomreservation.reservationservice.validation.rules.TimeRangeRule;
import com.roomreservation.reservationservice.validation.rules.WorkingHoursRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindBusyRoomsValidationStrategy implements ReservationValidationStrategy<HasTimeRange> {

    private final ReservationValidationChain<HasTimeRange> chain = new ReservationValidationChain<>(
            List.of(
                    new TimeRangeRule(),
                    new WorkingHoursRule()
            )
    );

    @Override
    public void validate(HasTimeRange request) {
        chain.validate(request);
    }

}
