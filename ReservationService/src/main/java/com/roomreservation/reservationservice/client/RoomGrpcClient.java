package com.roomreservation.reservationservice.client;

import com.roomreservation.contracts.room.grpc.GetRoomSummariesRequest;
import com.roomreservation.contracts.room.grpc.RoomGrpcServiceGrpc;
import com.roomreservation.contracts.room.grpc.RoomSummary;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomGrpcClient {

    @GrpcClient("roomService")
    private RoomGrpcServiceGrpc.RoomGrpcServiceBlockingStub stub;

    public List<RoomSummary> getRoomSummaries(List<Long> ids) {
        var req = GetRoomSummariesRequest.newBuilder()
                .addAllRoomIds(ids)
                .build();

        return stub.getRoomSummaries(req).getRoomsList();
    }
}
