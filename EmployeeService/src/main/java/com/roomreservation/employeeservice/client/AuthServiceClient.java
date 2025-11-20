package com.roomreservation.employeeservice.client;

import com.roomreservation.employeeservice.dto.DisableUserRequest;
import com.roomreservation.employeeservice.dto.InviteUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestClient authRestClient;

    public void inviteUser(InviteUserRequest request) {
        authRestClient.post()
                .uri("/api/auth/invite")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void disableUserByEmployeeId(Long employeeId) {
        authRestClient.patch()
                .uri("/api/auth/users/disable")
                .body(new DisableUserRequest(employeeId))
                .retrieve()
                .toBodilessEntity();
    }
}
