package com.roomreservation.authenticationservice.service;

import com.roomreservation.authenticationservice.model.User;

public interface JwtService {

    String generateToken(User user);

}