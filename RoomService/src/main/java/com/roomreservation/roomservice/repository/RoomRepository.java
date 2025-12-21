package com.roomreservation.roomservice.repository;

import com.roomreservation.roomservice.model.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends SoftDeleteRepository<Room, Long> {

    @Query("select r.id from Room r where r.id in :ids and r.deleted = false")
    List<Long> findExistingIds(@Param("ids") List<Long> ids);

}
