package com.znz.tpip_backend.dto;

import java.time.LocalDate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.znz.tpip_backend.enums.EducationLevel;

import com.znz.tpip_backend.enums.InternStatus;

import lombok.Data;

@Data
public class InternDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    private EducationLevel educationLevel;
    
    private String specialization;

    private int graduationYear;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private InternStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    // fk
    private Long applicationId;

    // reverse r/ships;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long certificateId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long placementId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> activityLogIds;
    
    // UI display
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String applicantName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String applicantEmail;

    // Evaluation Summary Data
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // private Integer totalEvaluations;
    
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // private Double latestEvaluationScore;

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // private Double averageEvaluationScore;
    
}
