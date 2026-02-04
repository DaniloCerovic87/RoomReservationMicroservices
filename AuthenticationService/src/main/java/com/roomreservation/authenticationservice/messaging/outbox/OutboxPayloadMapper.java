package com.roomreservation.authenticationservice.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OutboxPayloadMapper {

    private final ObjectMapper mapper;

    public OutboxPayloadMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize event to JSON", e);
        }
    }

    public <T> T fromJson(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Cannot deserialize event JSON", e);
        }
    }
}
