package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProgrammeChoiceDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Integer preferenceRank; 

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer matchScore;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isEligible;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String eligibilityRemark;

    // fk
    private Long applicationId;
    private Long programmeId;
}