package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.EducationLevel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "education")
public class Education extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationLevel level;

    @Column(nullable = false)
    private String institutionName;

    private String programmeName; // Only for Diploma/Degree

    @Column(nullable = false)
    private Integer completionYear;

    // ================= ACADEMIC RESULTS =================
    private String gpa;

    private String classification; // First Class, Second Class, etc.

    // ================= OPTIONAL DESCRIPTION =================
    // @Column(columnDefinition = "TEXT")
    // private String description;

    @Column(nullable = false)
    private Boolean isVerified = false;

    // fk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    // reverse
    @OneToMany(mappedBy = "education", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationSubject> subjects = new ArrayList<>();

    @OneToMany(mappedBy = "education", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

}