package com.roomreservation.roomservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("AMPHITHEATER")
@Getter
@Setter
public class Amphitheater extends Room {

    @NotNull
    private Integer numberOfProjectors;
}
