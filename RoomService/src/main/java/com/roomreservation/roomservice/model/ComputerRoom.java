package com.roomreservation.roomservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("COMPUTER_ROOM")
@Getter
@Setter
public class ComputerRoom extends Room {

    @NotNull
    private Integer numberOfComputers;
}
