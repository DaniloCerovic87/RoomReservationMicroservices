package com.roomreservation.reservationservice.interceptor;

import com.roomreservation.reservationservice.security.SecurityTokenProvider;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtGrpcClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> AUTH =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final SecurityTokenProvider tokenProvider;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next
    ) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String token = tokenProvider.currentTokenOrNull();
                if (token != null) {
                    headers.put(AUTH, "Bearer " + token);
                }
                super.start(responseListener, headers);
            }
        };
    }
}
