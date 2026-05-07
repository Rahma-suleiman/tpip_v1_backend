package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.AdminState;
import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.enums.ApplicationStep;

@Entity
@Table(name = "applications", uniqueConstraints = {
        /*
         * PURPOSE:
         * Ensures that an applicant can only submit ONE application per intake.
         *
         * HOW IT WORKS:
         * The combination of (applicant_id + intake_id) must always be unique in the
         * table.
         *
         * This is NOT about individual column uniqueness — it is about the PAIR.
         *
         * EXAMPLE:
         *
         * applicant_id | intake_id | status
         * -----------------------------------
         * 1 | 2024 | OK
         * 1 | 2025 | OK
         * 1 | 2024 | ❌ NOT ALLOWED (duplicate pair)
         * 2 | 2024 | OK
         *
         * RESULT:
         * - Same applicant can apply in different intakes
         * - Same intake can have many applicants
         * - BUT same applicant cannot apply twice in the same intake
         */
        @UniqueConstraint(columnNames = { "applicant_id", "intake_id" })
})
@Getter
@Setter
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String applicationIndexNumber;

    // Step tracking (important for wizard)
    @Enumerated(EnumType.STRING)
    private ApplicationStep currentStep = ApplicationStep.PERSONAL_INFO;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    private boolean locked = false;

    private LocalDateTime submittedAt;

    private LocalDateTime lockedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminState adminState = AdminState.SUBMITTED;
    
    // fk
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @ManyToOne
    @JoinColumn(name = "intake_id", nullable = false)
    private Intake intake;

    // reverse
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Referee> referees = new ArrayList<>();

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgrammeChoice> programmeChoices = new ArrayList<>();

    // BELOW METHODS R 4 ADMIN REVIEW PROCESS
    public boolean hasMissingDocuments() {
        return false; // implement based on documents
    }

    public boolean isAdminReviewComplete() {
        return true;
    }

    public boolean requiresInterview() {
        return false;
    }

    public boolean isEligibleForApproval() {
        return true;
    }

    public boolean isRejected() {
        return false;
    }

    public boolean isBorderline() {
        return false;
    }

    public void addAdminComment(String comment) {
        // implement comment entity later
    }
}
// User → Applicant → Active Intake → Application

// FINAL SYSTEM FLOW (REAL ADMISSION SYSTEM)
// PHASE 1: ACCOUNT & SETUP
// 1. User registers / logs in
// 2. Applicant profile is created (1:1 with User)
// 3. System auto-fetches ACTIVE intake (no user selection)

// PHASE 2: APPLICATION INITIALIZATION
// 4. System checks:
// → Does Application exist for (Applicant + Active Intake)?

// NO → Create new Application (status = DRAFT)
// YES → Resume existing Application

// PHASE 3: APPLICATION WIZARD (CORE PROCESS)
// 5. Applicant fills multi-step wizard:

// Step 1: Personal Info
// Step 2: Education
// Step 3: Work Experience (optional)
// Step 4: Programme Choice
// Step 5: Referees (optional in your current design)
// Step 6: Payment (optional in your current design)
// Step 7: Review

// PHASE 4: SUBMISSION
// 6. System validates required steps
// 7. Applicant submits application
// 8. Application status → SUBMITTED
// 9. Application is LOCKED

// PHASE 5: ADMIN WORKFLOW
// 10. Admin reviews application → UNDER_REVIEW
// 11. Interview scheduled (optional)
// 12. Interview conducted
// 13. Final decision:
// → ACCEPTED / REJECTED / WAITLISTED
// 14. If accepted → PLACED