package com.roomreservation.calendarservice.dto;

import java.time.LocalDateTime;

public record CalendarEntryDto(
        Long roomId,
        String roomName,
        Long reservationId,
        Long employeeId,
        String employeeName,
        String reservationName,
        String reservationType,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
