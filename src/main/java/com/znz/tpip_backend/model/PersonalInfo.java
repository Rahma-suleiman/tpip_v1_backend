package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.*;

@Getter
@Setter
@Entity
@Table(name = "personal_info")
public class PersonalInfo extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= BASIC INFO =================
    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String nationality;

    // ================= CONTACT =================
    @Column(nullable = false)
    private String phoneNumber;

    private String alternativePhone;

    @Column(nullable = false)
    private String email;

    // ================= ADDRESS =================
    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private District district;

    // private String ward;
    // private String street;

    // ================= NEXT OF KIN =================
    @Embedded
    private NextOfKin nextOfKin;

    // ================= DISABILITY =================
    @Embedded
    private Disability disability;


    // fk
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, unique = true)
    private Applicant applicant;
    
    // reverse
    @OneToMany(mappedBy = "personalInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();
}