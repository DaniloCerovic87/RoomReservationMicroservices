package com.roomreservation.reservationservice.dto;

import java.time.LocalDateTime;

public interface HasTimeRange {
    LocalDateTime startTime();
    LocalDateTime endTime();
}
