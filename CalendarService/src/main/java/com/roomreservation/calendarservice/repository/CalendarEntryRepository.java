package com.roomreservation.calendarservice.repository;

import com.roomreservation.calendarservice.model.CalendarEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarEntryRepository extends MongoRepository<CalendarEntry, ObjectId> {

    List<CalendarEntry> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime dayEnd, LocalDateTime dayStart);

}
