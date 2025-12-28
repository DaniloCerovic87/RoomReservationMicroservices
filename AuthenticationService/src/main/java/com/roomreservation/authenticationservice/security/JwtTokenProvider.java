package com.roomreservation.authenticationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtUserPrincipal getPrincipalFromToken(String token) throws JwtException {
        Claims claims = parseClaims(token);

        Long employeeId = null;
        Object empIdObj = claims.get("sub");
        if (empIdObj instanceof Number number) {
            employeeId = number.longValue();
        }

        String role = claims.get("role", String.class);

        return new JwtUserPrincipal(employeeId, role);
    }

    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
