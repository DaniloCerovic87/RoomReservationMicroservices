package com.roomreservation.authenticationservice.controller;

import com.roomreservation.authenticationservice.dto.InviteUserRequest;
import com.roomreservation.authenticationservice.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteUser(@Valid @RequestBody InviteUserRequest request) {
        authService.inviteUser(request);
        return ResponseEntity.ok().build();
    }

}
