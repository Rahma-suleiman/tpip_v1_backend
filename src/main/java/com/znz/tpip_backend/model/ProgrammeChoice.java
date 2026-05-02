package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "programme_choices")
public class ProgrammeChoice extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Preference rank = order of priority chosen by the applicant
    // It tells the system:“Which programme do I want MOST, second, and third?”
    @Column(nullable = false)
    private Integer preferenceRank; // 1, 2, 3(order of preference)

    private Integer matchScore;

    // ================= ELIGIBILITY =================
    private Boolean isEligible;

    @Column(columnDefinition = "TEXT")
    private String eligibilityRemark;

    // ================= OVERRIDE =================
    // private Boolean overrideRequested = false;

    // @Column(columnDefinition = "TEXT")
    // private String overrideReason;

    // fk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "programme_id", nullable = false)
    private Programme programme;
}

// GOAL (What we’re building)
// Instead of:
// ❌ Not eligible → override?
// We do:
// ❌ Not eligible → Here are better programmes for you ✅