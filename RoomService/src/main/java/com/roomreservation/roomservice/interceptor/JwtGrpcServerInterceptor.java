package com.roomreservation.roomservice.interceptor;

import com.roomreservation.roomservice.security.JwtTokenProvider;
import com.roomreservation.roomservice.security.JwtUserPrincipal;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@GrpcGlobalServerInterceptor
@Component
@RequiredArgsConstructor
public class JwtGrpcServerInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> AUTH =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        String authHeader = headers.get(AUTH);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            call.close(Status.UNAUTHENTICATED.withDescription("Missing Authorization"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        String token = authHeader.substring(7);

        try {
            JwtUserPrincipal principal = jwtTokenProvider.getPrincipalFromToken(token);
            if (principal == null || principal.role() == null) {
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), new Metadata());
                return new ServerCall.Listener<>() {};
            }

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()));
            var authentication = new UsernamePasswordAuthenticationToken(principal, token, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(
                    next.startCall(call, headers)
            ) {
                @Override public void onComplete() {
                    SecurityContextHolder.clearContext();
                    super.onComplete();
                }
                @Override public void onCancel() {
                    SecurityContextHolder.clearContext();
                    super.onCancel();
                }
            };

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            call.close(Status.UNAUTHENTICATED.withDescription("Token validation failed"), new Metadata());
            return new ServerCall.Listener<>() {};
        }
    }
}
