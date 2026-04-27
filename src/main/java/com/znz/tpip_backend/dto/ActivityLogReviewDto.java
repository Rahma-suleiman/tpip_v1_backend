package com.znz.tpip_backend.dto;

import com.znz.tpip_backend.enums.ActivityLogStatus;

import lombok.Data;

@Data
public class ActivityLogReviewDto {
    private String mentorComment;
    private ActivityLogStatus status;
}
