package com.znz.tpip_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.EvaluationStatus;
import com.znz.tpip_backend.enums.EvaluationType;

import lombok.Data;

@Data
public class EvaluationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private double score;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EvaluationStatus status;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate evaluationDate;

    private EvaluationType evaluationType;

    private String remarks;

    //  SUMMARY DATA
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double averageRating;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer totalSessions;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer totalHours;

    // fk(input)
    private Long placementId;

    // UI display
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long internId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String internName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long mentorId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mentorName;

    // Reverse
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long extensionId;
}
