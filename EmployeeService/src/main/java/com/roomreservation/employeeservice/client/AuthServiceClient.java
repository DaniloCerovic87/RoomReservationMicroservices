package com.roomreservation.employeeservice.client;

import com.roomreservation.employeeservice.dto.DisableUserRequest;
import com.roomreservation.employeeservice.dto.InviteUserRequest;
import com.roomreservation.employeeservice.exception.client.AuthServiceException;
import com.roomreservation.employeeservice.exception.client.AuthServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final RestClient authRestClient;
    @Value("${auth-service.retry.max-attempts}")
    private int MAX_ATTEMPTS;
    @Value("${auth-service.retry.initial-backoff-ms}")
    private int INITIAL_BACKOFF_MS;

    public void inviteUser(InviteUserRequest request) {
        executeAuthCall(() ->
                authRestClient.post()
                        .uri("/api/auth/invite")
                        .body(request)
                        .retrieve()
                        .toBodilessEntity()
        );
    }

    public void disableUserByEmployeeId(Long employeeId) {
        executeAuthCall(() ->
                authRestClient.patch()
                        .uri("/api/auth/users/disable")
                        .body(new DisableUserRequest(employeeId))
                        .retrieve()
                        .toBodilessEntity());
    }

    private <T> void executeAuthCall(Supplier<T> action) {
        long backoff = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++, backoff *= 2) {
            try {
                action.get();
                return;
            } catch (RestClientResponseException ex) {
                log.warn("AuthService 5xx on attempt {}/{}. Retrying...",
                        attempt, MAX_ATTEMPTS, ex);
                if (ex.getStatusCode().is5xxServerError() && attempt < MAX_ATTEMPTS) {
                    sleep(backoff);
                    backoff *= 2;
                    continue;
                }

                throw new AuthServiceException(
                        "Error response from AuthService",
                        HttpStatus.valueOf(ex.getStatusCode().value()),
                        ex.getResponseBodyAsString(),
                        ex
                );
            } catch (ResourceAccessException ex) {
                log.warn("AuthService unreachable on attempt {}/{}. Retrying...",
                        attempt, MAX_ATTEMPTS, ex);
                if (attempt < MAX_ATTEMPTS) {
                    sleep(backoff);
                    backoff *= 2;
                    continue;
                }

                throw new AuthServiceUnavailableException(
                        "AuthService is unreachable",
                        ex
                );
            }
        }
    }

    private void sleep(long backoffMs) {
        try {
            Thread.sleep(backoffMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
