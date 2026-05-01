package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.ReviewDecision;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationReviewDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Long applicationId;

    private String reviewerName;
    private String comments;
    private ReviewDecision decision;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime reviewedAt;
}