package com.roomreservation.roomservice.service;

import com.roomreservation.roomservice.dto.RoomRequest;
import com.roomreservation.roomservice.dto.RoomResponse;

import java.util.List;

public interface RoomService {

    List<RoomResponse> getAllRooms();

    RoomResponse getRoomById(Long id);

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteRoom(Long id);

}