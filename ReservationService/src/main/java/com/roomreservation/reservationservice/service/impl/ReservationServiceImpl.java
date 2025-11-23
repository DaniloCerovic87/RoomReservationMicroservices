package com.roomreservation.reservationservice.service.impl;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;
import com.roomreservation.reservationservice.model.Reservation;
import com.roomreservation.reservationservice.model.enums.ClassType;
import com.roomreservation.reservationservice.model.enums.ExamType;
import com.roomreservation.reservationservice.model.enums.ReservationPurpose;
import com.roomreservation.reservationservice.model.enums.ReservationStatus;
import com.roomreservation.reservationservice.repository.ReservationRepository;
import com.roomreservation.reservationservice.service.ReservationService;
import com.roomreservation.reservationservice.util.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        ReservationValidator.validateReservationDuration(request);

        // TODO: REST calls to validate room and employee ids

        Reservation reservation = new Reservation();
        reservation.setEmployeeId(request.employeeId());
        reservation.setRoomId(request.roomId());
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        reservation.setReservationPurpose(ReservationPurpose.fromValue(request.reservationPurpose()));
        reservation.setReservationStatus(ReservationStatus.PENDING);

        applyPurposeSpecificFields(reservation, request);

        reservationRepository.save(reservation);

        // TODO: emit event za CalendarService

        return ReservationResponse.toResponse(reservation);
    }

    private void applyPurposeSpecificFields(Reservation reservation, ReservationRequest request) {
        ReservationPurpose purpose = reservation.getReservationPurpose();

        switch (purpose) {
            case CLASS -> {
                ReservationValidator.validateClassReservation(request);
                reservation.setSubject(request.subject());
                reservation.setSemester(request.semester());
                reservation.setClassType(ClassType.fromValue(request.classType()));

            }
            case EXAM -> {
                ReservationValidator.validateExamReservation(request);
                reservation.setSubject(request.subject());
                reservation.setSemester(request.semester());
                reservation.setExamType(ExamType.fromValue(request.examType()));

            }
            case MEETING -> {
                ReservationValidator.validateMeetingReservation(request);
                reservation.setMeetingName(request.meetingName());
                reservation.setMeetingDescription(request.meetingDescription());
            }
            case EVENT -> {
                ReservationValidator.validateEventReservation(request);
                reservation.setEventName(request.eventName());
                reservation.setEventDescription(request.eventDescription());
            }
        }
    }
}