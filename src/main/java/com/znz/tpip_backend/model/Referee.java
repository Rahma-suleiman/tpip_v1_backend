package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;

import com.znz.tpip_backend.enums.RefereeStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "referees")
public class Referee extends AuditModel<String>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= RELATIONSHIP =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // ================= REFEREE INFO =================
    @Column(nullable = false)
    private String fullName;

    private String title;

    private String institution;

    @Column(nullable = false)
    private String email;

    private String phone;

    private String relationshipToApplicant;

    // ================= INVITATION SYSTEM =================
    private String inviteToken;

    private Boolean inviteSent = false;

    private Boolean linkUsed = false;

    // ================= STATUS =================
    @Enumerated(EnumType.STRING)
    private RefereeStatus status = RefereeStatus.PENDING;
}
// FLOW
// 1. Applicant adds referee details
// 2. System generates inviteToken
// 3. System sends email link
// 4. Referee opens secure form
// 5. Referee submits evaluation
// 6. System updates status:

//    PENDING → INVITED → SUBMITTED
//                  ↓
//               OVERDUE (if timeout)
//                  ↓
//               REMINDED (if re-sent)