package com.znz.tpip_backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.EducationLevel;

import lombok.Data;

@Data
public class EducationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private EducationLevel level;
    private String institutionName;
    private String programmeName;
    private Integer completionYear;

    private String gpa;
    private String classification;
    private String description;

    private Long applicantId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isVerified;

    // Subjects (for O-Level / A-Level)
    private List<EducationSubjectDto> subjects;

    // Documents (READ ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> documentIds;
}
