package com.roomreservation.reservationservice.service.impl;

import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryResponse;
import com.roomreservation.contracts.room.grpc.RoomSummary;
import com.roomreservation.reservationservice.client.EmployeeGrpcClient;
import com.roomreservation.reservationservice.client.RoomGrpcClient;
import com.roomreservation.reservationservice.dto.BusyRoomsRequest;
import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;
import com.roomreservation.reservationservice.exception.ResourceNotFoundException;
import com.roomreservation.reservationservice.exception.ValidationException;
import com.roomreservation.reservationservice.messaging.event.ReservationCreatedEvent;
import com.roomreservation.reservationservice.messaging.event.ReservationStatusChangedEvent;
import com.roomreservation.reservationservice.messaging.outbox.OutboxEventEnqueuer;
import com.roomreservation.reservationservice.model.Reservation;
import com.roomreservation.reservationservice.model.ReservationRoom;
import com.roomreservation.reservationservice.model.enums.ReservationStatus;
import com.roomreservation.reservationservice.model.enums.ReservationType;
import com.roomreservation.reservationservice.repository.ReservationRepository;
import com.roomreservation.reservationservice.repository.ReservationRoomRepository;
import com.roomreservation.reservationservice.service.ReservationService;
import com.roomreservation.reservationservice.validation.ReservationValidationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final EmployeeGrpcClient employeeGrpcClient;
    private final RoomGrpcClient roomGrpcClient;
    private final OutboxEventEnqueuer outboxEventEnqueuer;
    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final ReservationValidationStrategy<ReservationRequest> createReservationStrategy;
    private final ReservationValidationStrategy<BusyRoomsRequest> findBusyRoomsStrategy;

    public static void validateTransition(ReservationStatus current, ReservationStatus target) {
        if (!current.canTransitionTo(target)) {
            throw new ValidationException("INVALID_STATUS_TRANSITION");
        }
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        createReservationStrategy.validate(request);

        GetEmployeeSummaryResponse empResp = employeeGrpcClient.getEmployeeSummary(request.employeeId());
        if (!empResp.getExists()) {
            throw new ValidationException("EMPLOYEE_NOT_FOUND");
        }

        List<RoomSummary> roomSummaries = roomGrpcClient.getRoomSummaries(request.roomIds());

        if (request.roomIds().size() != roomSummaries.size()) {
            throw new ValidationException("ROOM_NOT_FOUND");
        }

        boolean conflict = reservationRepository.existsOverlappingReservation(request.roomIds(), request.startTime(), request.endTime());

        if (conflict) {
            throw new ValidationException("ROOM_ALREADY_RESERVED");
        }

        Reservation reservation = new Reservation();
        reservation.setEmployeeId(request.employeeId());
        reservation.setReservationName(request.reservationName());
        reservation.setReservationType(ReservationType.fromValue(request.reservationType()));
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        reservation.setReservationStatus(ReservationStatus.PENDING);
        Reservation saved = reservationRepository.save(reservation);

        List<ReservationRoom> links = request.roomIds().stream().map(roomId -> {
            ReservationRoom rr = new ReservationRoom();
            rr.setReservation(saved);
            rr.setRoomId(roomId);
            return rr;
        }).toList();

        reservationRoomRepository.saveAll(links);
        enqueueCreatedEvent(saved, empResp, roomSummaries);
        return ReservationResponse.toResponse(saved, request.roomIds(), empResp.getFullName());
    }

    private void enqueueCreatedEvent(Reservation reservation, GetEmployeeSummaryResponse empResp, List<RoomSummary> roomSummaries) {
        List<ReservationCreatedEvent.RoomSnapshot> roomSnapshots = roomSummaries.stream()
                .map(r -> new ReservationCreatedEvent.RoomSnapshot(r.getRoomId(), r.getName())).toList();

        ReservationCreatedEvent createdEvent = new ReservationCreatedEvent(reservation.getId(),
                new ReservationCreatedEvent.EmployeeSnapshot(empResp.getEmployeeId(), empResp.getFullName()),
                reservation.getReservationName(),
                reservation.getReservationType().getValue(),
                reservation.getReservationStatus().getValue(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                LocalDateTime.now(),
                roomSnapshots);

        outboxEventEnqueuer.enqueueReservationEvent(
                reservation.getId(),
                "ReservationCreatedEvent",
                "reservation-created",
                createdEvent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveReservationForRoom(Long roomId) {
        return reservationRepository.existsActiveReservationForRoom(roomId);
    }

    @Override
    @Transactional
    public void approveReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RESERVATION_NOT_FOUND"));
        changeStatus(reservation, ReservationStatus.APPROVED);
        enqueueStatusChangedEvent(reservation.getId(), ReservationStatus.APPROVED);
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void declineReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RESERVATION_NOT_FOUND"));
        changeStatus(reservation, ReservationStatus.DECLINED);
        enqueueStatusChangedEvent(reservation.getId(), ReservationStatus.DECLINED);
        reservationRepository.save(reservation);
    }

    private void changeStatus(Reservation reservation, ReservationStatus target) {
        ReservationStatus current = reservation.getReservationStatus();
        validateTransition(current, target);
        reservation.setReservationStatus(target);
    }

    private void enqueueStatusChangedEvent(Long reservationId, ReservationStatus newStatus) {
        ReservationStatusChangedEvent evt = new ReservationStatusChangedEvent(
                reservationId,
                newStatus.getValue(),
                LocalDateTime.now()
        );

        outboxEventEnqueuer.enqueueReservationEvent(
                reservationId,
                "ReservationStatusChangedEvent",
                "reservation-status-changed",
                evt
        );
    }

    @Override
    public List<Long> findBusyRoomIds(BusyRoomsRequest request) {
        findBusyRoomsStrategy.validate(request);
        return reservationRoomRepository.findBusyRoomIds(request.startTime(), request.endTime());
    }
}