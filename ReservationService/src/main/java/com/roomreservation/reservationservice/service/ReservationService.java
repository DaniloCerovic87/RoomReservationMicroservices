package com.roomreservation.reservationservice.service;

import com.roomreservation.reservationservice.dto.BusyRoomsRequest;
import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;
import com.roomreservation.reservationservice.dto.ReviewReservationRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);

    boolean hasActiveReservationForRoom(Long roomId);

    void reviewReservationRooms(Long reservationId, @Valid ReviewReservationRequest request);

    List<Long> findBusyRoomIds(BusyRoomsRequest request);

}