package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.*;

@Getter
@Setter
@Entity
@Table(name = "applications")
public class Application extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    private LocalDateTime submissionDate;

    // fk
    @OneToOne
    @JoinColumn(name = "applicant_id", nullable = false, unique = true)
    private Applicant applicant;

    // reverse
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgrammeChoice> programmeChoices = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Referee> referees = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    // @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Review> reviews = new ArrayList<>();

    // @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<StatusLog> statusLogs = new ArrayList<>();

    // @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Comment> comments = new ArrayList<>();

    // @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    // private Payment payment;

    // @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    // private Interview interview;

    // @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    // private Placement placement;
}

// User
//  └── Applicant
//        ├── PersonalInfo (Step 1)
//        ├──Education (Step 2)
//                 ├── level (O-Level / A-Level / Diploma / Degree)
//                 ├── institutionName
//                 ├── programmeName
//                 ├── completionYear
//                 ├── GPA / Classification
//                 ├── description
//                 ├── subjects (1:N)
//                 │      ├── subjectName
//                 │      ├── grade
//                 │
//                 └── documents (1:N)
//                ├── transcript
//                ├── certificate
//        ├── WorkExperience
//        └── Application
//               ├── Intake
//               ├── ProgrammeChoice
//               ├── Referee (1:N)
                        // ├── fullName
                        // ├── email
                        // ├── institution
                        // ├── inviteToken
                        // ├── status
                        // │
                        // └── RefereeSubmission (1:1)
                        //     ├── rating
                        //     ├── narrative
                        //     └── declaration
//               ├── Document
//               ├── Payment
//               ├── Review
//               ├── Interview
//               ├── Placement
//               ├── StatusLog
//               └── Comment
// CORRECT DOMAIN STRUCTURE (
// User → Applicant (1:1)

// Applicant → Application (1:1 per intake)

// Applicant → Education (1:N)

// Applicant → WorkExperience (1:N)

// Application → ProgrammeChoice (1:N max 3)

// Application → Referee (1:N min 2)

// Application → Payment (1:1)

// Application → Review (1:N)

// Application → Interview (0..1)

// Application → Placement (0..1)

// Application → StatusLog (1:N)

// Application → Document (1:N)

// Application → Comment (1:N)

// Document → optionally linked to Education OR Application