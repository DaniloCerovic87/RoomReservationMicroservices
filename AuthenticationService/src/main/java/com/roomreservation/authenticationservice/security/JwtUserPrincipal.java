package com.roomreservation.authenticationservice.security;

public record JwtUserPrincipal(
        Long employeeId,
        String role
) {
}
