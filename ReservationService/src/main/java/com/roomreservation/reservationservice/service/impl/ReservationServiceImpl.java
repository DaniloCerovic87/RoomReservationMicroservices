package com.roomreservation.reservationservice.service.impl;

import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryResponse;
import com.roomreservation.contracts.room.grpc.RoomSummary;
import com.roomreservation.reservationservice.client.EmployeeGrpcClient;
import com.roomreservation.reservationservice.client.RoomGrpcClient;
import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;
import com.roomreservation.reservationservice.exception.ValidationException;
import com.roomreservation.reservationservice.model.Reservation;
import com.roomreservation.reservationservice.model.ReservationRoom;
import com.roomreservation.reservationservice.model.enums.ReservationStatus;
import com.roomreservation.reservationservice.model.enums.ReservationType;
import com.roomreservation.reservationservice.repository.ReservationRepository;
import com.roomreservation.reservationservice.repository.ReservationRoomRepository;
import com.roomreservation.reservationservice.service.ReservationService;
import com.roomreservation.reservationservice.util.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final EmployeeGrpcClient employeeGrpcClient;
    private final RoomGrpcClient roomGrpcClient;
    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

//        ReservationValidator.validateForCreate(request);

        GetEmployeeSummaryResponse empResp = employeeGrpcClient.getEmployeeSummary(request.employeeId());
        if (!empResp.getExists()) {
            throw new ValidationException("Employee does not exist: " + request.employeeId());
        }

        List<RoomSummary> roomSummaries = roomGrpcClient.getRoomSummaries(request.roomIds());

        if (request.roomIds().size() != roomSummaries.size()) {
            throw new ValidationException("One or more rooms do not exist or are deleted");
        }

        boolean conflict = reservationRepository.existsOverlappingReservation(
                request.roomIds(), request.startTime(), request.endTime()
        );

        if (conflict) {
            throw new ValidationException("One or more chosen rooms are already reserved in the requested time slot");
        }

        Reservation reservation = new Reservation();
        reservation.setEmployeeId(request.employeeId());
        reservation.setReservationName(request.reservationName());
        reservation.setReservationType(ReservationType.fromValue(request.reservationType()));
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        reservation.setReservationStatus(ReservationStatus.PENDING);
        Reservation saved = reservationRepository.save(reservation);

        List<ReservationRoom> links = request.roomIds().stream()
                .distinct()
                .map(roomId -> {
                    ReservationRoom rr = new ReservationRoom();
                    rr.setReservationId(saved.getId());
                    rr.setRoomId(roomId);
                    return rr;
                })
                .toList();

        reservationRoomRepository.saveAll(links);

        // TODO: emit event za CalendarService

        return ReservationResponse.toResponse(reservation);
    }

    @Override
    public boolean hasActiveReservationForRoom(Long roomId) {
        return reservationRepository.existsActiveReservationForRoom(roomId);
    }

}