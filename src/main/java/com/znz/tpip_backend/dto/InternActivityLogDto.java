package com.znz.tpip_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.ActivityLogStatus;

import lombok.Data;

@Data
public class InternActivityLogDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private LocalDate date;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate reviewDate;

    private String subject;
    private String topicTaught;
    private String classLevel;

    private String activitiesDone;
    private String challenges;

    private Double hoursSpent;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mentorComment;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ActivityLogStatus status;

    // FK
    private Long internId;

    // UI only (read-only)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String internName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long mentorId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mentorName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long schoolId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String schoolName;
}