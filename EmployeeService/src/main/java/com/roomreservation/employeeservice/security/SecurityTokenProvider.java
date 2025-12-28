package com.roomreservation.employeeservice.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityTokenProvider {

    public String currentTokenOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object credentials = auth.getCredentials();
        if (credentials instanceof String t && !t.isBlank()) {
            return t;
        }
        return null;
    }

}
