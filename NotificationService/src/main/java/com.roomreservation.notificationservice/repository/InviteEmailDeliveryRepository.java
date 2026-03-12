package com.roomreservation.notificationservice.repository;

import com.roomreservation.notificationservice.model.InviteEmailDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteEmailDeliveryRepository extends JpaRepository<InviteEmailDelivery, Long> {
}