package com.roomreservation.reservationservice.service;

import com.roomreservation.reservationservice.dto.BusyRoomsRequest;
import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);

    boolean hasActiveReservationForRoom(Long roomId);

    void approveReservation(Long id);

    void declineReservation(Long id);

    List<Long> findBusyRoomIds(BusyRoomsRequest request);
}