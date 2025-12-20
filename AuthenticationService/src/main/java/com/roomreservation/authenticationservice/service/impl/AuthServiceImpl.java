package com.roomreservation.authenticationservice.service.impl;

import com.roomreservation.authenticationservice.dto.AuthResponse;
import com.roomreservation.authenticationservice.dto.CompleteRegistrationRequest;
import com.roomreservation.authenticationservice.dto.InviteUserRequest;
import com.roomreservation.authenticationservice.dto.LoginRequest;
import com.roomreservation.authenticationservice.event.UserInvitedEvent;
import com.roomreservation.authenticationservice.exception.*;
import com.roomreservation.authenticationservice.model.User;
import com.roomreservation.authenticationservice.model.enums.Role;
import com.roomreservation.authenticationservice.model.enums.UserStatus;
import com.roomreservation.authenticationservice.repository.UserRepository;
import com.roomreservation.authenticationservice.security.JwtService;
import com.roomreservation.authenticationservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, UserInvitedEvent> kafkaTemplate;

    @Transactional
    public void inviteUser(InviteUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(Role.USER);
        user.setStatus(UserStatus.PENDING_ACTIVATION);
        user.setEmployeeId(request.employeeId());
        user.setActivationToken(generateActivationToken());
        user.setActivationExpiresAt(LocalDateTime.now().plusDays(2));
        userRepository.save(user);

        UserInvitedEvent event = new UserInvitedEvent(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getActivationToken(),
                user.getActivationExpiresAt()
        );
        
        kafkaTemplate.send("user-invited", String.valueOf(user.getId()), event)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish user.invited for userId={}", user.getId(), ex);
                    } else {
                        log.info("Published user.invited for userId={} partition={} offset={}",
                                user.getId(),
                                res.getRecordMetadata().partition(),
                                res.getRecordMetadata().offset());
                    }
                });
    }

    private String generateActivationToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public void completeRegistration(CompleteRegistrationRequest request) {
        User user = userRepository.findByActivationToken(request.token())
                .orElseThrow(() -> new InvalidActivationTokenException(request.token()));

        if (user.getActivationExpiresAt() != null &&
                user.getActivationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ActivationTokenExpiredException(user.getActivationExpiresAt());
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPasswordHash(encodedPassword);

        user.setStatus(UserStatus.ACTIVE);
        user.setActivationToken(null);
        user.setActivationExpiresAt(null);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Transactional
    @Override
    public void disableUserByEmployeeId(Long employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found for employeeId = " + employeeId));

        if (user.getStatus() != UserStatus.DISABLED) {
            user.setStatus(UserStatus.DISABLED);
            userRepository.save(user);
        }
    }

}
