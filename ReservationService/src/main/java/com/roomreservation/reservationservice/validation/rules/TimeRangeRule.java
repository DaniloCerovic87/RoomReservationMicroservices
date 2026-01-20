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
            errors.add("Reservation end time must be after start time");
            return;
        }

        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 15) {
            errors.add("Reservation must be at least 15 minutes long");
        }
    }
}
