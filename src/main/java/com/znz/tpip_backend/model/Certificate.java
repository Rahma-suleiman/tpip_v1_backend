package com.znz.tpip_backend.model;

import java.time.LocalDate;

import com.znz.tpip_backend.enums.Grade;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "certificates")
public class Certificate extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Certificate details
    private String certificateNumber;   //Auto-generate Certificate Number. Example: TPIP-2026-0001

    private LocalDate issueDate;

    private String issuedBy; // e.g. DTE Zanzibar/ Ministry

    // Performance summary
    @Enumerated(EnumType.STRING)
    private Grade grade; 

    private String remarks;

    private Boolean approved;

    // fk
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intern_id", unique = true)
    private Intern intern;
}