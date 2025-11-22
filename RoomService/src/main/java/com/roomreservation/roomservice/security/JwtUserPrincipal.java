package com.roomreservation.roomservice.security;

public record JwtUserPrincipal(
        Long employeeId,
        String role
) {
}
