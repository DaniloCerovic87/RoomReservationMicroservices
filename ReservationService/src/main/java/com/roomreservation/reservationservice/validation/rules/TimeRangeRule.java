package com.roomreservation.reservationservice.validation.rules;

import com.roomreservation.reservationservice.dto.HasTimeRange;
import com.roomreservation.reservationservice.validation.ReservationRule;

import java.time.Duration;
import java.util.List;

public class TimeRangeRule implements ReservationRule<HasTimeRange> {

    @Override
    public void validate(HasTimeRange request, List<String> errors) {
        var start = request.startTime();
        var end = request.endTime();

        if (!end.isAfter(start)) {
            errors.add("END_TIME_MUST_BE_AFTER_START_TIME");
            return;
        }

        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 15) {
            errors.add("RESERVATION_TOO_SHORT");
        }
    }
}
