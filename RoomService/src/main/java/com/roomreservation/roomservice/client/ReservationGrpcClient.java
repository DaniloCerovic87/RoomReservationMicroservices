package com.roomreservation.roomservice.client;

import com.roomreservation.contracts.reservation.grpc.HasActiveReservationsForRoomRequest;
import com.roomreservation.contracts.reservation.grpc.ReservationGrpcServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class ReservationGrpcClient {

    @GrpcClient("reservationService")
    private ReservationGrpcServiceGrpc.ReservationGrpcServiceBlockingStub stub;

    public boolean hasActiveReservationsForRoom(Long roomId) {
        var req = HasActiveReservationsForRoomRequest.newBuilder()
                .setRoomId(roomId)
                .build();

        return stub.hasActiveReservationsForRoom(req).getHasActive();
    }
}
