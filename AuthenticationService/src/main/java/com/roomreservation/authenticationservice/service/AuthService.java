package com.roomreservation.authenticationservice.service;

import com.roomreservation.authenticationservice.dto.AuthResponse;
import com.roomreservation.authenticationservice.dto.CompleteRegistrationRequest;
import com.roomreservation.authenticationservice.dto.InviteUserRequest;
import com.roomreservation.authenticationservice.dto.LoginRequest;
import jakarta.validation.Valid;

public interface AuthService {

    void inviteUser(InviteUserRequest request);

    void completeRegistration(@Valid CompleteRegistrationRequest request);

    AuthResponse login(LoginRequest request);

    void disableUserByEmployeeId(Long employeeId);
}
