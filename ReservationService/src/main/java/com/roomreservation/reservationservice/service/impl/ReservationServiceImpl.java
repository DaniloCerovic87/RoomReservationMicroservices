package com.roomreservation.reservationservice.service.impl;

import com.roomreservation.contracts.employee.grpc.GetEmployeeSummaryResponse;
import com.roomreservation.contracts.room.grpc.RoomSummary;
import com.roomreservation.reservationservice.client.EmployeeGrpcClient;
import com.roomreservation.reservationservice.client.RoomGrpcClient;
import com.roomreservation.reservationservice.dto.BusyRoomsRequest;
import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;
import com.roomreservation.reservationservice.dto.ReviewReservationRequest;
import com.roomreservation.reservationservice.exception.ResourceNotFoundException;
import com.roomreservation.reservationservice.exception.ValidationException;
import com.roomreservation.reservationservice.messaging.event.ReservationCreatedEvent;
import com.roomreservation.reservationservice.messaging.event.ReservationRoomStatusChangedEvent;
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
import java.util.*;
import java.util.stream.Collectors;

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
        Reservation saved = reservationRepository.save(reservation);

        List<ReservationRoom> links = request.roomIds().stream().map(roomId -> {
            ReservationRoom rr = new ReservationRoom();
            rr.setReservation(saved);
            rr.setRoomId(roomId);
            rr.setReservationStatus(ReservationStatus.PENDING);
            return rr;
        }).toList();

        reservationRoomRepository.saveAll(links);
        enqueueCreatedEvent(saved, empResp, roomSummaries);
        return ReservationResponse.toResponse(saved, request.roomIds(), empResp.getFullName());
    }

    private void enqueueCreatedEvent(Reservation reservation, GetEmployeeSummaryResponse empResp, List<RoomSummary> roomSummaries) {
        List<ReservationCreatedEvent.RoomSnapshot> roomSnapshots = roomSummaries.stream()
                .map(r -> new ReservationCreatedEvent.RoomSnapshot(r.getRoomId(), r.getName(), ReservationStatus.PENDING.getValue())).toList();

        ReservationCreatedEvent createdEvent = new ReservationCreatedEvent(reservation.getId(),
                new ReservationCreatedEvent.EmployeeSnapshot(empResp.getEmployeeId(), empResp.getFullName()),
                reservation.getReservationName(),
                reservation.getReservationType().getValue(),
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
    public void reviewReservationRooms(Long reservationId, ReviewReservationRequest request) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("RESERVATION_NOT_FOUND"));

        Set<Long> approveSet = new HashSet<>(request.approveRoomIds());

        Map<Long, String> declineMap = new HashMap<>();
        for (var d : request.declineRooms()) {
            if (declineMap.putIfAbsent(d.roomId(), d.comment().trim()) != null) {
                throw new ValidationException("DUPLICATE_ROOM_ID_IN_DECLINE_LIST");
            }
        }
        Set<Long> declineSet = declineMap.keySet();

        Set<Long> intersection = new HashSet<>(approveSet);
        intersection.retainAll(declineSet);
        if (!intersection.isEmpty()) {
            throw new ValidationException("ROOM_ID_IN_BOTH_APPROVE_AND_DECLINE");
        }

        Set<Long> reservationRoomIds = reservation.getReservationRooms().stream()
                .map(ReservationRoom::getRoomId)
                .collect(Collectors.toSet());

        Set<Long> requested = new HashSet<>();
        requested.addAll(approveSet);
        requested.addAll(declineSet);

        if (!reservationRoomIds.containsAll(requested)) {
            throw new ValidationException("ROOM_NOT_PART_OF_RESERVATION");
        }

        for (ReservationRoom rr : reservation.getReservationRooms()) {
            Long roomId = rr.getRoomId();

            if (approveSet.contains(roomId)) {
                changeStatus(rr, ReservationStatus.APPROVED);
                enqueueRoomStatusChangedEvent(reservationId, roomId, ReservationStatus.APPROVED);
            } else if (declineMap.containsKey(roomId)) {
                changeStatus(rr, ReservationStatus.DECLINED);

                String comment = declineMap.get(roomId);
                rr.setReviewComment(comment);

                enqueueRoomStatusChangedEvent(reservationId, roomId, ReservationStatus.DECLINED);
            }
        }

        reservationRepository.save(reservation);
    }


    private void changeStatus(ReservationRoom reservationRoom, ReservationStatus target) {
        ReservationStatus current = reservationRoom.getReservationStatus();
        validateTransition(current, target);
        reservationRoom.setReservationStatus(target);
    }

    private void enqueueRoomStatusChangedEvent(Long reservationId, Long roomId, ReservationStatus newStatus) {
        ReservationRoomStatusChangedEvent evt = new ReservationRoomStatusChangedEvent(
                reservationId,
                roomId,
                newStatus.getValue(),
                LocalDateTime.now()
        );

        outboxEventEnqueuer.enqueueReservationEvent(
                reservationId,
                "ReservationRoomStatusChangedEvent",
                "reservation-room-status-changed",
                evt
        );
    }

    @Override
    public List<Long> findBusyRoomIds(BusyRoomsRequest request) {
        findBusyRoomsStrategy.validate(request);
        return reservationRoomRepository.findBusyRoomIds(request.startTime(), request.endTime());
    }
}