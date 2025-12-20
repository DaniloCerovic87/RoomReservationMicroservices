package com.roomreservation.roomservice.repository;

import com.roomreservation.roomservice.model.Room;

public interface RoomRepository extends SoftDeleteRepository<Room, Long> {
}
