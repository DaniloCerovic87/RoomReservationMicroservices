package com.roomreservation.roomservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("CLASSROOM")
@Getter
@Setter
public class Classroom extends Room {

    private Boolean hasSmartBoard;

}
