package com.znz.tpip_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import com.znz.tpip_backend.enums.UserRole;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AuditModel<String> {
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login credentials
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Contact
    @Column(nullable = false)
    private String mobileNumber;

    // Role (Applicant, Reviewer, Admin, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Verification
    // private Boolean isVerified = false;

    // private String otpCode;
    // private LocalDateTime otpExpiry;

    // Account status
    private Boolean isActive = true;


    // Relationship with Applicant
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Applicant applicant;

}
// ALTER TABLE users ALTER COLUMN password DROP NOT NULL;

// @Inheritance(strategy = JOINED) means:

// “Store User, Intern, and Mentor in different tables,
// but connect them using the same ID and join them when needed.”

// What is @Inheritance?
// @Inheritance(strategy = InheritanceType.JOINED)

// 👉 This tells Spring Boot (JPA):

// “I have a parent class (User) and child classes (Intern, Mentor),
// so please store them in the database in a related way.”

// 🎯 Purpose (Simple Meaning)

// 👉 It helps you connect classes that use inheritance in Java
// to how data is stored in the database tables

// What JOINED Strategy Does
// @Inheritance(strategy = InheritanceType.JOINED)

// 👉 It creates separate tables, but connects them using the same ID