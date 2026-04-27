package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "referee_submissions")
public class RefereeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "referee_id", nullable = false, unique = true)
    private Referee referee;

    // ================= ASSESSMENT =================
    private Integer rating; // 1–5 scale

    @Column(columnDefinition = "TEXT")
    private String narrative;

    private Boolean authenticityDeclaration = true;
}