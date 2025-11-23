package com.roomreservation.reservationservice.util;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.exception.ValidationException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

@UtilityClass
public class ReservationValidator {

    public static void validateReservationDuration(ReservationRequest request) {
        long durationInMinutes = Duration.between(request.startTime(), request.endTime()).toMinutes();
        if (durationInMinutes < 5) {
            throw new ValidationException("Reservation must be at least 5 minutes long");
        }
    }

    public static void validateClassReservation(ReservationRequest request) {
        if (StringUtils.isBlank(request.subject())) {
            throw new ValidationException("Subject is required for class reservation");
        }
        if (request.semester() == null) {
            throw new ValidationException("Semester is required for class reservation");
        }
        if (StringUtils.isBlank(request.classType())) {
            throw new ValidationException("Class type is required for class reservation");
        }
    }

    public static void validateExamReservation(ReservationRequest request) {
        if (StringUtils.isBlank(request.subject())) {
            throw new ValidationException("Subject is required for exam reservation");
        }
        if (request.semester() == null) {
            throw new ValidationException("Semester is required for exam reservation");
        }
        if (StringUtils.isBlank(request.examType())) {
            throw new ValidationException("Exam type is required for exam reservation");
        }
    }

    public static void validateMeetingReservation(ReservationRequest request) {
        if (StringUtils.isBlank(request.meetingName())) {
            throw new ValidationException("Meeting name is required for meeting reservation");
        }
        if (StringUtils.isBlank(request.meetingDescription())) {
            throw new ValidationException("Meeting description is required for meeting reservation");
        }
    }

    public static void validateEventReservation(ReservationRequest request) {
        if (StringUtils.isBlank(request.eventName())) {
            throw new ValidationException("Event name is required for event reservation");
        }
        if (StringUtils.isBlank(request.eventDescription())) {
            throw new ValidationException("Event description is required for event reservation");
        }
    }
}
