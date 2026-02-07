package com.roomreservation.calendarservice.repository;

import com.roomreservation.calendarservice.model.CalendarEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CalendarEntryRepository extends MongoRepository<CalendarEntry, String> {

    List<CalendarEntry> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime dayEnd, LocalDateTime dayStart);

    Optional<CalendarEntry> findByReservationIdAndRoomId(Long reservationId, Long roomId);
}
