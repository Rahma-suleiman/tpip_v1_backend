package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.znz.tpip_backend.enums.*;

@Data
@Entity
@Table(name = "interns")
public class Intern extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    private String specialization;

    private int graduationYear;

    @Enumerated(EnumType.STRING)
    private InternStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    // fk
    // Intern belongs to a user
    // @OneToOne
    // @JoinColumn(name = "user_id", nullable = false, unique = true)
    // private User user;

    // Intern → Application (ONLY) User accessed via Application. so no need to have direct r/ship between Intern → User. It creates redundancy.
    @OneToOne
    @JoinColumn(name = "application_id", nullable = false,unique = true)
    private Application application;

    // reverse r/ship
    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private Certificate certificate;

    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private Placement placement;

    @OneToMany(mappedBy = "intern", cascade = CascadeType.ALL)
    private List<InternActivityLog> activityLogs;

    @OneToMany(mappedBy = "intern", fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();  //Do NOT include feedback in InternDto to hide it in responses. Feedback should be accessed via FeedbackController, not via InternController. This way we can control access to feedback data and ensure that only authorized users (e.g., mentors) can view it.
}