package com.znz.tpip_backend.dto;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationProgressDto {

    private Long applicationId;
    private Long applicantId;

    private ApplicationStep currentStep;
    private ApplicationStep nextStep;   // ✅ ADDED (important for resume flow)

    private ApplicationStatus status;

    private boolean locked;

    private int progressPercentage;

    private String currentStepLabel;

    private String nextStepLabel;       // ✅ ADDED (UI friendly)

    private boolean canResume;          // ✅ ADDED (resume control flag)
}