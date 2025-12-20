package com.roomreservation.roomservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@DiscriminatorValue("COMPUTER_ROOM")
@SQLDelete(sql = "UPDATE room SET deleted = true WHERE id = ?")
@Getter
@Setter
public class ComputerRoom extends Room {

    @NotNull
    private Integer numberOfComputers;
}
