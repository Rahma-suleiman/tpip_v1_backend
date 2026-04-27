package com.znz.tpip_backend.model;

import com.znz.tpip_backend.enums.DocumentType;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "documents")
public class Document extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    //  // Optional: document can belong to education (transcript/certificate)
    // @ManyToOne
    // @JoinColumn(name = "education_id", nullable = false)
    // private Education education;

    // Document type (ID, Certificate, Transcript)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    // File path or URL
    // private String fileUrl;
    @Column(nullable = false)
    private String fileUrl;

    // Verification status
    private Boolean isVerified = false;
}