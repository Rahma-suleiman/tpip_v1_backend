package com.znz.tpip_backend.dto;

import lombok.Data;

@Data
public class ProgrammeRecommendationDto {

    private Long programmeId;
    private String programmeName;
    private Integer matchScore;
    private String reason;
}