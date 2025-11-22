package com.roomreservation.roomservice.service.impl;


import com.roomreservation.roomservice.dto.RoomRequest;
import com.roomreservation.roomservice.dto.RoomResponse;
import com.roomreservation.roomservice.exception.ResourceNotFoundException;
import com.roomreservation.roomservice.model.Room;
import com.roomreservation.roomservice.model.enums.RoomType;
import com.roomreservation.roomservice.repository.RoomRepository;
import com.roomreservation.roomservice.service.RoomService;
import com.roomreservation.roomservice.util.RoomValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponse::fromEntity)
                .toList();
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return RoomResponse.fromEntity(room);
    }

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        RoomValidator.validateRoom(request.roomType(), request.numberOfComputers());
        Room room = Room.builder()
                .name(request.name())
                .roomType(RoomType.fromValue(request.roomType()))
                .capacity(request.capacity())
                .numberOfComputers(request.numberOfComputers())
                .build();

        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        RoomValidator.validateRoom(request.roomType(), request.numberOfComputers());
        room.setName(request.name());
        room.setRoomType(RoomType.fromValue(request.roomType()));
        room.setCapacity(request.capacity());
        room.setNumberOfComputers(room.getRoomType().equals(RoomType.COMPUTER_ROOM) ? request.numberOfComputers() : null);
        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room", id);
        }

        // TODO send request to reservation service to check if there is a reservation

        roomRepository.deleteById(id);
    }

}
