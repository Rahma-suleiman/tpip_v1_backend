package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor // Generates a no-argument constructor i.e public Applicant() {}
@AllArgsConstructor
@Builder
@Entity
@Table(name = "applicants")
public class Applicant extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // fk
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

   
    @Column(nullable = false, unique = true, updatable = false)
    private String indexNumber;

    private Boolean hasWorkExperience;

    // reverse
    @OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL)
    private PersonalInfo personalInfo;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<WorkExperience> workExperiences = new ArrayList<>();
}

// 1. @NoArgsConstructor (IMPORTANT ✅)
// ✔ Purpose
// Generates a no-argument constructor
// i.e
// public Applicant() {}
// ✔ Why you NEED it
// JPA (Hibernate) requires a default constructor to:
// Instantiate entities via reflection
// Load data from the database
// If you remove it:
// You may get errors like:
// No default constructor for entity
// 👉 Conclusion: REQUIRED for JPA

// 2. @AllArgsConstructor (OPTIONAL ⚠️)
// ✔ Purpose
// Generates constructor with all fields
// public Applicant(Long id, User user, String firstName, ...) {}
// ✔ When useful
// Testing
// Manual object creation
// DTO mapping (sometimes)

// 3.What is @Builder?
// implements the Builder Design Pattern.
// 👉 Its purpose is to let you create objects step-by-step, instead of using
// long, confusing constructors.

// The Problem It Solves
// Without @Builder, you might do this:
// Applicant applicant = new Applicant(
// 1L,
// user,
// "Rahma",
// null,
// "Suleiman",
// LocalDate.of(2000, 1, 1),
// Gender.FEMALE,
// "Tanzanian",
// "rahma@email.com",
// null,
// "0712345678",
// "Dar es Salaam",
// "Amina",
// "0711111111",
// "Mother",
// false,
// null
// );
// Problems:
// Hard to read
// Easy to mix values
// Not maintainable
// Breaks when fields change

// What @Builder Does
// It lets you write this instead:
// Applicant applicant = Applicant.builder()
// .firstName("Rahma")
// .lastName("Suleiman")
// .gender(Gender.FEMALE)
// .mobileNumber("0712345678")
// .build();
// ✔ Benefits:
// Readable
// Flexible
// Safe (no wrong order issues)
// Only set what you need

// When You SHOULD Use @Builder
// Use it when:
// Your class has many fields (like your Applicant)
// You want clean service layer code
// You’re mapping DTO → Entity
// You want to avoid constructor confusion
// When NOT to Use It
// Avoid it when:
// Class has very few fields (2–3 only)
// You need strict required fields enforcement (builder doesn’t enforce by
// default)