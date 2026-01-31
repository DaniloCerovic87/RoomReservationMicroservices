package com.roomreservation.reservationservice.model.enums;

import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    DECLINED("Declined");

    private final String value;

    public boolean canTransitionTo(ReservationStatus target) {
        return switch (this) {
            case PENDING -> target == APPROVED || target == DECLINED;
            case APPROVED, DECLINED -> false;
        };
    }

}
