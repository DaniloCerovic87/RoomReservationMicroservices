package com.roomreservation.reservationservice.validation;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationValidationChain {

    private final List<ReservationRule> rules;

    public void validate(ReservationRequest request) {
        List<String> errors = new ArrayList<>();

        for (ReservationRule rule : rules) {
            rule.validate(request, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
