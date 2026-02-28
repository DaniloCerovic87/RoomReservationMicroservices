package com.roomreservation.notificationservice.repository;

import com.roomreservation.notificationservice.model.SentInviteEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentInviteEmailRepository extends JpaRepository<SentInviteEmail, Long> {
}