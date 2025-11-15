package com.roomreservation.authenticationservice.service;

import com.roomreservation.authenticationservice.dto.InviteUserRequest;
import com.roomreservation.authenticationservice.model.User;
import com.roomreservation.authenticationservice.model.enums.Role;
import com.roomreservation.authenticationservice.model.enums.UserStatus;
import com.roomreservation.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;

    @Transactional
    public void inviteUser(InviteUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with given email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("User with given username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        user.setStatus(UserStatus.PENDING_ACTIVATION);
        user.setEmployeeId(request.getEmployeeId());
        user.setActivationToken(generateActivationToken());
        user.setActivationExpiresAt(LocalDateTime.now().plusDays(2));

        userRepository.save(user);
    }

    private String generateActivationToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
