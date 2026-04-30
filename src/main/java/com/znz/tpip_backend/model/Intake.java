package com.znz.tpip_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intakes")
@Getter
@Setter
public class Intake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. "2025/2026"

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean active = false; // ONLY ONE SHOULD BE TRUE
}
// User
//   ↓ 1:1
// Applicant
//   ↓ 1:M
// Application
//   ↓ M:1
// Intake


//   {
//     "name": "2024/2025",
//     "startDate": "2024-10-01T08:00:00Z",
//     "endDate": "2025-06-30T17:00:00Z",
//     "active": true
//   }
//   {
//     "name": "2025/2026",
//     "startDate": "2025-10-01T08:00:00Z",
//     "endDate": "2026-06-30T17:00:00Z",
//     "active": false
//   }
//   {
//     "name": "2026/2027",
//     "startDate": "2026-10-01T08:00:00Z",
//     "endDate": "2027-06-30T17:00:00Z",
//     "active": false
//   }
