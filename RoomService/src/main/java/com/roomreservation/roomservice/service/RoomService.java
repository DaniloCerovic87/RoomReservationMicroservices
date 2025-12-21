package com.roomreservation.roomservice.service;

import com.roomreservation.contracts.room.grpc.RoomSummary;
import com.roomreservation.roomservice.dto.RoomCreateRequest;
import com.roomreservation.roomservice.dto.RoomResponse;
import com.roomreservation.roomservice.dto.RoomUpdateRequest;

import java.util.List;

public interface RoomService {

    List<RoomResponse> getAllRooms();

    RoomResponse getRoomById(Long id);

    RoomResponse createRoom(RoomCreateRequest request);

    RoomResponse updateRoom(Long id, RoomUpdateRequest request);

    void deleteRoom(Long id);

    List<RoomSummary> getRoomSummaries(List<Long> roomIds);

}