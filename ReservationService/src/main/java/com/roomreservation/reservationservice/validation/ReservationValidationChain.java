package com.roomreservation.reservationservice.validation;

import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationValidationChain<T> {

    private final List<ReservationRule<? super T>> rules;

    public void validate(T request) {
        List<String> errors = new ArrayList<>();

        for (ReservationRule<? super T> rule : rules) {
            rule.validate(request, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}