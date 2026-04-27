package com.znz.tpip_backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.znz.tpip_backend.enums.Region;
import com.znz.tpip_backend.enums.District;
import com.znz.tpip_backend.enums.SchoolType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "schools")
public class School extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location; 

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private District district;

    // // Stores ONLY ONE school type per school (e.g. PRIMARY OR SECONDARY)
    // // This is a simple column inside the School table
    // @Enumerated(EnumType.STRING)
    // private SchoolType schoolType;
    
  
    // Stores MULTIPLE school types per school (e.g. PRIMARY + SECONDARY)
    // Uses a separate table (school_types) linked to school
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "school_types", joinColumns = @JoinColumn(name = "school_id"))
    @Column(name = "type")
    private Set<SchoolType> schoolTypes;

    private int capacity; // max interns allowed

    private int currentInternCount = 0;

    private String phoneNumber;

    private String email;

    private String headTeacherName;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Placement> placements = new ArrayList<>();

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mentor> mentors = new ArrayList<>();
}