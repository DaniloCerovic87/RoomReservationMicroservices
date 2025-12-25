package com.roomreservation.calendarservice.repository;

import com.roomreservation.calendarservice.model.CalendarEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CalendarEntryRepository extends MongoRepository<CalendarEntry, String> {
}
