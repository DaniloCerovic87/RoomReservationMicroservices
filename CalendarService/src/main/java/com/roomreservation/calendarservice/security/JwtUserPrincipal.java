package com.roomreservation.calendarservice.security;

public record JwtUserPrincipal(
        Long employeeId,
        String role
) {
}
