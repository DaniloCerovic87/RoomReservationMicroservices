package com.roomreservation.reservationservice.security;

public record JwtUserPrincipal(
        Long employeeId,
        String role
) {
}
