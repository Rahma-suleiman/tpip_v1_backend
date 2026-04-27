package com.znz.tpip_backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.PlacementStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "placements")
public class Placement extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Internship duration
    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate assignedDate;

    @Enumerated(EnumType.STRING)
    private PlacementStatus status;

    private String remarks;

    // fk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intern_id", nullable = false, unique = true)
    private Intern intern;

    // reverse
    @OneToMany(mappedBy = "placement", cascade = CascadeType.ALL)
    private List<Extension> extensions = new ArrayList<>();

}