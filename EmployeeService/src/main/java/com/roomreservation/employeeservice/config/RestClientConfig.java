package com.roomreservation.employeeservice.config;

import com.roomreservation.employeeservice.interceptor.AuthTokenPropagationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${auth-service.url}")
    private String authServiceUrl;

    @Bean
    public RestClient authRestClient(RestClient.Builder builder,
                                     AuthTokenPropagationInterceptor interceptor) {
        return builder.baseUrl(authServiceUrl)
                .requestInterceptor(interceptor)
                .build();
    }

}
