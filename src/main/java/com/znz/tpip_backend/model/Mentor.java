package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.MentorStatus;
import com.znz.tpip_backend.enums.QualificationLevel;

@Entity
@Getter
@Setter
@Table(name = "mentors")
public class Mentor extends User {

    @Enumerated(EnumType.STRING)
    private QualificationLevel qualificationLevel;

    private int yearsOfExperience;

    private String name;

    private String specialization;

    @Enumerated(EnumType.STRING)
    private MentorStatus status;

    // fk r/ship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    // reverse r/ships
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Placement> placements = new ArrayList<>();

}