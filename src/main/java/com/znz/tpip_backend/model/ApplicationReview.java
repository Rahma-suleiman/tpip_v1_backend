package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.znz.tpip_backend.enums.ReviewDecision;

@Entity
@Table(name = "application_reviews")
@Getter
@Setter
public class ApplicationReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewerName;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Enumerated(EnumType.STRING)
    private ReviewDecision decision;

    private LocalDateTime reviewedAt;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;
}