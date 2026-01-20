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
            errors.add("Reservation must be within a single day.");
            return;
        }

        if (start.toLocalTime().isBefore(OPEN) || end.toLocalTime().isAfter(CLOSE)) {
            errors.add("Reservations are allowed only between 08:00 and 22:00.");
        }
    }
}
