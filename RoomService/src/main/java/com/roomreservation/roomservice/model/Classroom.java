package com.roomreservation.roomservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@DiscriminatorValue("CLASSROOM")
@SQLDelete(sql = "UPDATE room SET deleted = true WHERE id = ?")
@Getter
@Setter
public class Classroom extends Room {

    private Boolean hasSmartBoard;

}
