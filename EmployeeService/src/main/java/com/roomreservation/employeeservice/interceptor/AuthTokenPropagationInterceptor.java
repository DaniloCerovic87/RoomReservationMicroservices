package com.roomreservation.employeeservice.interceptor;

import com.roomreservation.employeeservice.security.SecurityTokenProvider;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthTokenPropagationInterceptor implements ClientHttpRequestInterceptor {

    private final SecurityTokenProvider tokenProvider;

    public AuthTokenPropagationInterceptor(SecurityTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] body, @NonNull ClientHttpRequestExecution execution)
            throws IOException {

        String token = tokenProvider.currentTokenOrNull();
        if (token != null) {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return execution.execute(request, body);
    }
}
