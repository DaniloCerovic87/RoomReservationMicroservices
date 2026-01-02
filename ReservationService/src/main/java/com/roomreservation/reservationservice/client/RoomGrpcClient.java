package com.roomreservation.reservationservice.client;

import com.roomreservation.contracts.room.grpc.GetRoomSummariesRequest;
import com.roomreservation.contracts.room.grpc.RoomGrpcServiceGrpc;
import com.roomreservation.contracts.room.grpc.RoomSummary;
import com.roomreservation.reservationservice.interceptor.JwtGrpcClientInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomGrpcClient {

    @GrpcClient("roomService")
    private RoomGrpcServiceGrpc.RoomGrpcServiceBlockingStub stub;

    private final JwtGrpcClientInterceptor jwtGrpcClientInterceptor;

    public List<RoomSummary> getRoomSummaries(List<Long> ids) {
        var req = GetRoomSummariesRequest.newBuilder()
                .addAllRoomIds(ids)
                .build();

        var securedStub = stub.withInterceptors(jwtGrpcClientInterceptor);
        return securedStub.getRoomSummaries(req).getRoomsList();
    }

}
