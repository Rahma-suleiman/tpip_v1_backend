package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProgrammeChoiceDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Integer preferenceRank;
    private Integer matchScore;

    private Boolean isEligible;
    private String eligibilityRemark;

    // fk
    private Long applicationId;
    private Long programmeId;
}