package com.znz.tpip_backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.MentorStatus;
import com.znz.tpip_backend.enums.QualificationLevel;

import lombok.Data;

@Data
public class MentorDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    private String name;

    private String email; 
    
    private QualificationLevel qualificationLevel;

    private int yearsOfExperience;

    private String specialization;

    private MentorStatus status;
    
    // fk r/ship
    private Long schoolId;

    // UI Display
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String schoolName; 

    // ===== SUMMARY DATA (IMPORTANT FOR UI) =====
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // private Integer totalEvaluationsGiven;

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // private Double averageEvaluationScore;

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // private Integer totalPlacements;
    
    // reverse r/ships
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> feedbackIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> placementIds ;

}
