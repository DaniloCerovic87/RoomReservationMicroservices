package com.roomreservation.reservationservice.model.enums;

import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    DECLINED("Declined"),
    CANCELLED("Cancelled");

    private final String value;

    public static ReservationStatus fromValue(String value) {
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new ValidationException("Unknown reservation status: {0}", value);
    }

    public boolean canTransitionTo(ReservationStatus target) {
        return switch (this) {
            case PENDING -> target == APPROVED || target == DECLINED || target == CANCELLED;
            case APPROVED -> target == CANCELLED;
            case DECLINED, CANCELLED -> false;
        };
    }

}
