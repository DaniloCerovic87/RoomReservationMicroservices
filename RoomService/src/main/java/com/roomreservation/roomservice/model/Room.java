package com.roomreservation.roomservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "room_type",
        discriminatorType = DiscriminatorType.STRING
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Integer capacity;

    @Transient
    public String getRoomType() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }

}