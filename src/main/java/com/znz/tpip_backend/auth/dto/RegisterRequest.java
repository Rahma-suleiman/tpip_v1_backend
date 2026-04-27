package com.znz.tpip_backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String mobileNumber;

    private String password;

    @Column(unique = true)
    private String indexNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String applicationIndexNumber;
}
// When a user registers:

// 👉 You MUST immediately create:

// User
// Applicant
// Application Index Number

// So your system becomes:

// User (login account)
// ↓
// Applicant (profile + index number)

// HOW FLOW WORKS NOW
// User sends RegisterRequest
// ↓
// System checks email,exam index
// ↓
// Creates User
// ↓
// Creates Applicant
// ↓
// Generates INDEX: INT/2026/00001
// ↓
// Links Applicant ↔ User
// ↓
// Saves everything
// ↓
// Returns application index to user