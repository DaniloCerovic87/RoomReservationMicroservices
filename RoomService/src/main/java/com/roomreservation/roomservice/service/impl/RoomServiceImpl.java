package com.roomreservation.roomservice.service.impl;


import com.roomreservation.contracts.room.grpc.RoomSummary;
import com.roomreservation.roomservice.client.ReservationGrpcClient;
import com.roomreservation.roomservice.dto.RoomCreateRequest;
import com.roomreservation.roomservice.dto.RoomResponse;
import com.roomreservation.roomservice.dto.RoomUpdateRequest;
import com.roomreservation.roomservice.exception.ResourceNotFoundException;
import com.roomreservation.roomservice.exception.ValidationException;
import com.roomreservation.roomservice.model.Amphitheater;
import com.roomreservation.roomservice.model.Classroom;
import com.roomreservation.roomservice.model.ComputerRoom;
import com.roomreservation.roomservice.model.Room;
import com.roomreservation.roomservice.model.enums.RoomType;
import com.roomreservation.roomservice.repository.RoomRepository;
import com.roomreservation.roomservice.service.RoomService;
import com.roomreservation.roomservice.util.RoomValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final ReservationGrpcClient reservationGrpcClient;
    private final RoomRepository roomRepository;

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAllByDeletedFalse().stream()
                .map(RoomResponse::fromEntity)
                .toList();
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return RoomResponse.fromEntity(room);
    }

    @Override
    public RoomResponse createRoom(RoomCreateRequest request) {
        RoomValidator.validateCreate(
                request.roomType(),
                request.numberOfComputers(),
                request.numberOfProjectors(),
                request.hasSmartBoard()
        );

        Room room;

        switch (RoomType.fromValue(request.roomType())) {
            case COMPUTER_ROOM -> {
                ComputerRoom computerRoom = new ComputerRoom();
                computerRoom.setNumberOfComputers(request.numberOfComputers());
                room = computerRoom;
            }
            case AMPHITHEATER -> {
                Amphitheater amphitheater = new Amphitheater();
                amphitheater.setNumberOfProjectors(request.numberOfProjectors());
                room = amphitheater;
            }
            case CLASSROOM -> {
                Classroom classroom = new Classroom();
                classroom.setHasSmartBoard(request.hasSmartBoard());
                room = classroom;
            }
            default -> throw new IllegalStateException("Unexpected room type: " + request.roomType());
        }

        room.setName(request.name());
        room.setCapacity(request.capacity());
        Room saved = roomRepository.save(room);

        return RoomResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomUpdateRequest request) {
        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        RoomValidator.validateUpdate(room, request);

        if (request.name() != null) {
            room.setName(request.name());
        }

        if (request.capacity() != null) {
            room.setCapacity(request.capacity());
        }

        if (room instanceof ComputerRoom computerRoom) {
            if (request.numberOfComputers() == null || request.numberOfComputers() <= 0) {
                throw new ValidationException("Computer room must have a valid number of computers");
            }
            computerRoom.setNumberOfComputers(request.numberOfComputers());
        }

        if (room instanceof Amphitheater amphitheater) {
            if (request.numberOfProjectors() == null || request.numberOfProjectors() <= 0) {
                throw new ValidationException("Amphitheater must have at least one projector");
            }
            amphitheater.setNumberOfProjectors(request.numberOfProjectors());
        }

        if (room instanceof Classroom classroom) {
            if (request.hasSmartBoard() == null) {
                throw new ValidationException("Classroom must define smart board availability");
            }
            classroom.setHasSmartBoard(request.hasSmartBoard());
        }

        Room updated = roomRepository.save(room);

        return RoomResponse.fromEntity(updated);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        if (reservationGrpcClient.hasActiveReservationsForRoom(id)) {
            throw new ValidationException("Room can't be deleted because it has active reservations.");
        }

        roomRepository.delete(room);
    }

    @Override
    public List<RoomSummary> getRoomSummaries(List<Long> roomIds) {
        List<Room> rooms = roomRepository.findByIdInAndDeletedFalse(roomIds);
        return rooms.stream().map(r -> RoomSummary.newBuilder().setRoomId(r.getId())
                .setName(r.getName())
                .build()).toList();
    }

}
