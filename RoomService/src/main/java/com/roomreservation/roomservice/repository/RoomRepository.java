package com.roomreservation.roomservice.repository;

import com.roomreservation.roomservice.model.Room;
import java.util.Collection;
import java.util.List;

public interface RoomRepository extends SoftDeleteRepository<Room, Long> {

    List<Room> findByIdInAndDeletedFalse(Collection<Long> ids);

}
