package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.znz.tpip_backend.enums.RefereeStatus;

@Entity
@Table(name = "referees")
@Getter
@Setter
public class Referee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String title;
    private String organization;
    private String email;
    private String phone;
    private String relationship;

    // recommendation data (Step 5 form)
    private Integer rating;
    private String narrative;
    private boolean declarationAccepted;

    private String token;
    private LocalDateTime tokenExpiry;
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private RefereeStatus status = RefereeStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;
}