package com.znz.tpip_backend.enums;
public enum ApplicationStatus {

    // Applicant stage
    DRAFT,          // filling form

    // Submission
    SUBMITTED,      // submitted by applicant

    // Review process
    UNDER_REVIEW,   // being reviewed

    // Interview stage (optional)
    INTERVIEW_SCHEDULED,
    INTERVIEWED,

    // Final decisions
    ACCEPTED,
    REJECTED,

    // Post decision
    PLACED
}