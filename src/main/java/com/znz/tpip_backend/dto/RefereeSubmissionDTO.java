package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RefereeSubmissionDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Long refereeId;

    private Integer rating;

    private String narrative;

    private Boolean authenticityDeclaration;
}
