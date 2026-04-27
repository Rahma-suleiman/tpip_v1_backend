package com.znz.tpip_backend.model;

import java.time.LocalDate;

import com.znz.tpip_backend.enums.ActivityLogStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "intern_activity_logs")
public class InternActivityLog extends AuditModel<String>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Date of teaching activity
    private LocalDate date;

    private LocalDate reviewDate; 

    // Lesson details
    private String subject;         
    
    private String topicTaught;

    private String classLevel;       // e.g. Form 2, Grade 5

    //  Activity details
    @Column(length = 1000)
    private String activitiesDone;

    @Column(length = 1000)
    private String challenges;

    private Double hoursSpent;

    @Column(length = 1000)
    private String mentorComment;

    @Enumerated(EnumType.STRING)
    private ActivityLogStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intern_id", nullable = false)
    private Intern intern;    
    
    // get mentor like this: intern.getPlacement().getMentor()
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "mentor_id", nullable = false)
    // private Mentor mentor;           
}