package com.roomreservation.authenticationservice.repository;

import com.roomreservation.authenticationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByActivationToken(String activationToken);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmployeeId(Long employeeId);
}
