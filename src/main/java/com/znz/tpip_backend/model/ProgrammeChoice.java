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
public class ProgrammeChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= RELATIONSHIP =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // ================= PROGRAMME INFO =================
    // @ManyToOne
    // @JoinColumn(name = "programme_id", nullable = false)
    // private Programme programme;

    // ================= RANKING =================
    @Column(nullable = false)
    private Integer preferenceRank; // 1, 2, 3

    // ================= ELIGIBILITY =================
    private Boolean isEligible;

    @Column(columnDefinition = "TEXT")
    private String eligibilityRemark;

    // ================= OVERRIDE =================
    private Boolean overrideRequested = false;

    @Column(columnDefinition = "TEXT")
    private String overrideReason;
}