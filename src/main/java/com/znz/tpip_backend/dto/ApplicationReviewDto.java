package com.znz.tpip_backend.dto;

import com.znz.tpip_backend.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationReviewDto {

    private ApplicationStatus status;
    private String reviewerName;
    // private String comment; // WHY rejected/approved
}