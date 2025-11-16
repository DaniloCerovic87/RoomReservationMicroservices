package com.roomreservation.authenticationservice.controller;

import com.roomreservation.authenticationservice.dto.CompleteRegistrationRequest;
import com.roomreservation.authenticationservice.dto.InviteUserRequest;
import com.roomreservation.authenticationservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteUser(@Valid @RequestBody InviteUserRequest request) {
        authService.inviteUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<Void> completeRegistration(@Valid @RequestBody CompleteRegistrationRequest request) {
        authService.completeRegistration(request);
        return ResponseEntity.ok().build();
    }

}
