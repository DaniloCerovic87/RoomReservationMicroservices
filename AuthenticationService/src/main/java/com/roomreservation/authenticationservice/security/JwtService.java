package com.roomreservation.authenticationservice.security;

import com.roomreservation.authenticationservice.model.User;

public interface JwtService {

    String generateToken(User user);

}