package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.District;
import com.znz.tpip_backend.enums.EmploymentType;
import com.znz.tpip_backend.enums.Region;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "work_experience")
public class WorkExperience extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    // ================= EMPLOYER DETAILS =================
    @Column(nullable = false)
    private String employerName;

    private String employerAddress;

    private String employerPhone;

    private String employerEmail;

    // private String employerWebsite;

    // ================= JOB DETAILS =================
    @Column(nullable = false)
    private String jobTitle;

    private String department;

    // @Column(length = 1000)
    // private String jobDescription;

    @Column(length = 2000)
    private String responsibilities;

    // ================= EMPLOYMENT PERIOD =================
    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isCurrentlyEmployed = false;

    // ================= EMPLOYMENT TYPE =================
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    // ================= LOCATION =================
    private String country;
    private Region region;
    private District district;
    private String city;

    // ================= COMPENSATION =================
    // private Double monthlySalary;

    // private String currency;

    // ================= VERIFICATION =================
    private Boolean isVerified = false;

     // fk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    // ================= DOCUMENTS (IMPORTANT FOR STEP 3) =================
    @OneToMany(mappedBy = "workExperience", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

}