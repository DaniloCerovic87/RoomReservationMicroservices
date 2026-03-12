package com.roomreservation.employeeservice.messaging.listener;

import com.roomreservation.employeeservice.messaging.event.InviteEmailFailedFinalEvent;
import com.roomreservation.employeeservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InviteEmailFailedListener {

    private final EmployeeService employeeService;

    @KafkaListener(topics = "${app.kafka.topics.inviteEmailFailed:invite-email.failed}",
            groupId = "${spring.kafka.consumer.group-id:employee-service}")
    public void consume(InviteEmailFailedFinalEvent event) {
        employeeService.deleteEmployee(event.employeeId());
    }
}