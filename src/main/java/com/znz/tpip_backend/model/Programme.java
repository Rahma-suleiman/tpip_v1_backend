package com.znz.tpip_backend.model;

import com.znz.tpip_backend.enums.EducationLevel;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "programmes")
public class Programme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String department;

    private String field;

    private String location;

    private Integer availableSlots;

    private Integer durationMonths;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private EducationLevel requiredLevel;

    private Boolean isActive = true;
}