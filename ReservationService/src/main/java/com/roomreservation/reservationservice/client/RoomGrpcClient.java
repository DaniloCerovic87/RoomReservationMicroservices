package com.roomreservation.reservationservice.client;

import com.roomreservation.contracts.room.grpc.ExistsAllRoomsRequest;
import com.roomreservation.contracts.room.grpc.RoomGrpcServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomGrpcClient {

    @GrpcClient("roomService")
    private RoomGrpcServiceGrpc.RoomGrpcServiceBlockingStub stub;

    public boolean existsAllRooms(List<Long> ids) {
        var req = ExistsAllRoomsRequest.newBuilder()
                .addAllRoomIds(ids)
                .build();

        return stub.existsAllRooms(req).getExists();
    }
}
