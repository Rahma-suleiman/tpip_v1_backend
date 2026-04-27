package com.znz.tpip_backend.enums;

public enum ActivityLogStatus {

    DRAFT,          // intern is still editing
    SUBMITTED,      // intern has submitted
    // UNDER_REVIEW,   // mentor is currently reviewing
    APPROVED,       // mentor approves log
    NEEDS_REVISION, // mentor requests correction
    REJECTED        // mentor rejects completely
}
// Intern creates log → DRAFT
//         ↓
// Intern submits → SUBMITTED
//         ↓
// Mentor reviews:
//     → APPROVED ✅ (final)
//     → REJECTED ❌ (final)
//     → NEEDS_REVISION 🔁
//             ↓
//      System sets → DRAFT
//             ↓
//      Intern edits → SUBMITTED again