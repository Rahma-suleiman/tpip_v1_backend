package com.znz.tpip_backend.dto;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationProgressDto {

    private Long applicationId;
    private Long applicantId;

    private ApplicationStep currentStep;
    private ApplicationStatus status;

    private boolean locked;

    private int progressPercentage;
    private String currentStepLabel;
}
// (API RESPONSE) EXAMPLE
// {
//   "applicationId": 12,
//   "applicantId": 5,
//   "currentStep": "EDUCATION",
//   "status": "DRAFT",
//   "locked": false,
//   "progressPercentage": 33,
//   "currentStepLabel": "EDUCATION"
// }