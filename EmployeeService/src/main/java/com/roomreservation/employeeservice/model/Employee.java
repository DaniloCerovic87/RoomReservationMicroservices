package com.roomreservation.employeeservice.model;

import com.roomreservation.employeeservice.enums.AcademicRank;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "employee")
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id = ?")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String personalId;

    @NotBlank
    private String email;

    @NotBlank
    private String title;

    @Enumerated(EnumType.STRING)
    private AcademicRank academicRank;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private boolean deleted = false;

}
