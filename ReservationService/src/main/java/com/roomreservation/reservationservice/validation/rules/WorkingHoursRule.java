package com.roomreservation.reservationservice.validation.rules;

import com.roomreservation.reservationservice.dto.HasTimeRange;
import com.roomreservation.reservationservice.validation.ReservationRule;

import java.time.LocalTime;
import java.util.List;

public class WorkingHoursRule implements ReservationRule<HasTimeRange> {

    private static final LocalTime OPEN = LocalTime.of(8, 0);
    private static final LocalTime CLOSE = LocalTime.of(22, 0);

    @Override
    public void validate(HasTimeRange request, List<String> errors) {
        var start = request.startTime();
        var end = request.endTime();

        if (!start.toLocalDate().equals(end.toLocalDate())) {
            errors.add("RESERVATION_MUST_BE_WITHIN_SINGLE_DAY");
            return;
        }

        if (start.toLocalTime().isBefore(OPEN) || end.toLocalTime().isAfter(CLOSE)) {
            errors.add("RESERVATION_MUST_BE_WITHIN_SINGLE_DAY");
        }
    }
}
