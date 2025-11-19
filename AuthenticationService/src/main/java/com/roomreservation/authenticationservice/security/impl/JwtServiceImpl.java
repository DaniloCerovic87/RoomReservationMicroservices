package com.roomreservation.authenticationservice.security.impl;

import com.roomreservation.authenticationservice.model.User;
import com.roomreservation.authenticationservice.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-minutes:60}")
    private Long expirationMinutes;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);


        return Jwts.builder()
                .claims()
                .subject(String.valueOf(user.getEmployeeId()))
                .issuedAt(Date.from(expiry))
                .expiration(Date.from(expiry))
                .and()
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
}