package com.znz.tpip_backend.model;

import java.time.LocalDate;

import com.znz.tpip_backend.enums.EvaluationStatus;
import com.znz.tpip_backend.enums.EvaluationType;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "evaluations")
public class Evaluation extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score; // final score (0-100)

    @Enumerated(EnumType.STRING)
    private EvaluationStatus status;

    private String remarks;

    private LocalDate evaluationDate;

    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType;

        // ✅ SUMMARY DATA (derived from logs/feedback)
    private Double averageRating;     // from feedbacks
    private Integer totalSessions;    // number of logs
    private Integer totalHours;       // optional (from activity logs)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id", nullable = false)
    private Placement placement;
    
    @OneToOne(mappedBy = "evaluation", fetch = FetchType.LAZY)
    private Extension extension;
}
// Evaluation depends ONLY on Placement

// And through placement you can reach everything:

// Placement
//    ├── Intern
//    ├── Mentor
//    ├── Feedback (mentor → intern)
//    └── Activity Logs (intern work)

// So:

//  YES — placement alone is enough
// evaluation should be based on a combination of:

// Intern Activity Logs → what the intern actually did
// Mentor Feedback Logs → how well the intern performed
// DO NOT directly link Evaluation → ActivityLog or Feedback

// That would:

// Make DB messy ❌
// Cause duplication ❌
// Break scalability ❌

// ✔ Instead:

// Evaluation is linked to Placement, and through placement you can access BOTH logs