package com.znz.tpip_backend.enums;

public enum RefereeStatus {

    // Referee created but not contacted yet
    PENDING,

    // Email invitation sent successfully
    INVITED,

    // Reminder email sent
    REMINDED,

    // Referee has started or opened the form (optional UI tracking)
    IN_PROGRESS,

    // Final submission completed
    SUBMITTED,

    // Deadline passed without submission
    OVERDUE
}